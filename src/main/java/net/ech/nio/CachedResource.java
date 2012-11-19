package net.ech.nio;

import java.io.*;
import java.util.*;
import net.ech.util.*;

public class CachedResource
	extends ProxyResource
	implements Resource
{
	public final static int DEFAULT_FRESHNESS = 60;
	public final static int DEFAULT_BYTE_LIMIT = 10 * 1000 * 1000;

	private int freshness;
	private Map<String,Node> cache;

	public static class Config
	{
		private Resource resource;
		private int freshness = DEFAULT_FRESHNESS;
		private int byteLimit = DEFAULT_BYTE_LIMIT;

		public Resource getResource()
		{
			return resource;
		}

		public void setResource(Resource resource)
		{
			this.resource = resource;
		}

		public int getByteLimit()
		{
			return byteLimit;
		}

		public void setByteLimit(int byteLimit)
		{
			this.byteLimit = byteLimit;
		}

		/**
		 * Period in seconds since the time an item is cached that the resource will not
		 * re-query for it.
		 */
		public int getFreshness()
		{
			return freshness;
		}

		public void setFreshness(int freshness)
		{
			this.freshness = freshness;
		}
	}

	public CachedResource(Config config)
	{
		super(config.resource);
		this.freshness = config.freshness;
		this.cache = new LruCache<String,Node>(config.byteLimit, new Sizer<Node>() {
			@Override
			public int getSize(Node node) {
				return 32 + node.getByteCount();
			}
		});
	}

	@Override
	public ItemHandle resolve(Query query)
		throws IOException
	{
		return getNode(query).use(query);
	}

	private Node getNode(Query query)
	{
		Node node;
		String key = query.toString();
		synchronized (cache) {
			if (cache.containsKey(key)) {
				node = cache.get(key);
			}
			else {
				node = new Node();
				cache.put(key, node);
			}
		}
		return node;
	}

	private class Node
	{
		long timestamp;
		ItemHandle innerItem;
		byte[] bytes;
		IOException exception;

		public synchronized int getByteCount()
		{
			return bytes == null ? 0 : bytes.length;
		}

		synchronized ItemHandle use(Query query)
			throws IOException
		{
			if ((System.currentTimeMillis() - timestamp) > (freshness * 1000L)) {
				refresh(query);
			}
			if (exception != null) {
				throw exception;
			}
			return new ProxyItemHandle(innerItem) {

				@Override
				public InputStream openInputStream()
				{
					return new ByteArrayInputStream(bytes);
				}

				@Override
				public Reader openReader()
					throws IOException
				{
					return new InputStreamReader(openInputStream(), getCharacterEncoding());
				}

				private String getCharacterEncoding()
				{
					return getMetadata() == null ? null : getMetadata().getCharacterEncoding();
				}
			};
		}

		private void refresh(Query query)
		{
			try {
				this.timestamp = System.currentTimeMillis();
				this.innerItem = proxyResolve(query);
				this.bytes = getBytes(this.innerItem);
				this.exception = null;
			}
			catch (IOException e) {
				this.innerItem = null;
				this.bytes = null;
				this.exception = e;
			}
		}

		private byte[] getBytes(ItemHandle itemHandle)
			throws IOException
		{
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			BinaryPump.Config pumpConfig = new BinaryPump.Config();
			pumpConfig.setInputStream(itemHandle.openInputStream());
			pumpConfig.addOutputStream(buffer);
			pumpConfig.setErrorLog(new ErrorLog());  // TODO
			BinaryPump pump = new BinaryPump(pumpConfig);
			pump.run();
			return buffer.toByteArray();
		}
	}

	private ItemHandle proxyResolve(Query query)
		throws IOException
	{
		return super.resolve(query);
	}
}
