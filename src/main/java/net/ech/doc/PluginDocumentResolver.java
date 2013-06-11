package net.ech.doc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PluginDocumentResolver
	implements DocumentResolver
{
	private Map<String,Adapter> map = new HashMap<String,Adapter>();
	private Adapter defaultProtocolAdapter;
	private Adapter defaultAdapter;

	public void addResolver(String prefix, boolean stripPrefix, DocumentResolver resolver)
	{
		map.put(prefix, new Adapter(stripPrefix, resolver));
	}

	public void setDefaultProtocolResolver(DocumentResolver resolver)
	{
		this.defaultProtocolAdapter = new Adapter(false, resolver);
	}

	public void setDefaultResolver(DocumentResolver resolver)
	{
		this.defaultAdapter = new Adapter(false, resolver);
	}

	@Override
	public DocumentProducer resolve(String key)
		throws IOException
	{
		int prefixLen = getPrefixLength(key);
		Adapter adapter = getAdapter(key, prefixLen);
		if (adapter == null) {
			throw new IOException(key + ": no resolver");
		}
		return adapter.resolve(prefixLen >= 0 ? adapter.mutateDocumentKey(key) : key);
	}

	private static class Adapter
		extends ProxyDocumentResolver
	{
		private boolean stripPrefix;

		Adapter(boolean stripPrefix, DocumentResolver resolver)
		{
			super(resolver);
			this.stripPrefix = stripPrefix;
		}

		@Override
		protected String mutateDocumentKey(String key)
		{
			if (stripPrefix) {
				int prefixLen = getPrefixLength(key);
				if (prefixLen >= 0) {
					return key.substring(prefixLen + 1);
				}
			}
			return key;
		}
	}

	private static int getPrefixLength(String key)
	{
		return key.indexOf(':');
	}

	private Adapter getAdapter(String key, int prefixLen)
	{
		if (prefixLen < 0) {
			return defaultAdapter;
		}
		else {
			Adapter adapter = map.get(key.substring(0, prefixLen));
			return adapter == null ? defaultProtocolAdapter : adapter;
		}
	}
}
