package net.ech.io;

import java.io.IOException;
import java.util.*;

public class CachingContentSource
	extends ProxyContentSource
	implements ContentSource
{
	public final static int DEFAULT_FRESHNESS = 1000;
	private final static int DEFAULT_CACHE_SIZE = 40;

	private int cacheSize = DEFAULT_CACHE_SIZE;
	private int freshnessMillis = DEFAULT_FRESHNESS;
	private Map<String,Node> cache;

	public CachingContentSource(ContentSource inner)
	{
		super(inner);
	}

	public void setCacheSize(int cacheSize)
	{
		this.cacheSize = cacheSize;
		this.cache = null;
	}

	public void setFreshness(int freshnessMillis)
	{
		this.freshnessMillis = freshnessMillis;
	}

	@Override
	public ContentHandle resolve(ContentRequest request)
		throws IOException
	{
		init();
		return getNode(request).resolve();
	}

	private void init()
	{
		if (cache == null) {
			synchronized (this) {
				if (cache == null) {
					// Simple LRU cache:
					cache = new LinkedHashMap<String,Node>(cacheSize + 1, 1, false) {
						@Override
						protected boolean removeEldestEntry(Map.Entry<String,Node> eldest) {
							return size() > cacheSize;
						}
					};
				}
			}
		}
	}

	private Node getNode(final ContentRequest request)
	{
		String key = request.toString();
		Node node;
		synchronized (cache) {
			if (cache.containsKey(key)) {
				node = cache.get(key);
			}
			else {
				node = new Node(request);
				cache.put(key, node);
			}
		}
		return node;
	}

	private class Node
	{
		ContentRequest request;
		long timestamp;
		ContentHandle content;
		IOException exception;

		private Node(final ContentRequest request)
		{
			this.request = request;
		}

		public synchronized ContentHandle resolve()
			throws IOException
		{
			if ((System.currentTimeMillis() - timestamp) > freshnessMillis) {
				refresh();
			}
			if (exception != null) {
				throw exception;
			}
			return content;
		}

		private void refresh()
		{
			try {
				content = new BufferedContentHandle(proxyResolve(request));
				exception = null;
			}
			catch (IOException e) {
				content = null;
				exception = e;
			}
			timestamp = System.currentTimeMillis();
		}
	}

	private ContentHandle proxyResolve(final ContentRequest request)
		throws IOException
	{
		return super.resolve(request);
	}
}
