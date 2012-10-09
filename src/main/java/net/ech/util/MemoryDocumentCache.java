package net.ech.util;

import net.ech.util.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

public class MemoryDocumentCache
	implements DocumentCache
{
	private String[] keys;
	private int cacheSize;

	// Simple LRU cache:
	private Map<Map<String,Object>,Object> cache;
	
	public MemoryDocumentCache(String[] keys, int pCacheSize)
	{
		this.keys = keys;
		this.cacheSize = pCacheSize;
		this.cache = new LinkedHashMap<Map<String,Object>,Object>(pCacheSize + 1, 1, false) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<Map<String,Object>,Object> eldest) {
				return size() > cacheSize;
			}
		};
	}

	public int size()
	{
		return cache.size();
	}

    public Object getDocument(Map<String,Object> values)
        throws IOException
	{
		Map<String,Object> k = new Hash();
		for (String key : keys) {
			if (!values.containsKey(key)) {
				throw new DocumentException(key + " value required");
			}
			k.put(key, values.get(key));
		}
		return cache.get(k);
	}

    public void putDocument(Map<String,Object> document)
        throws IOException
	{
		Map<String,Object> k = new Hash();
		for (String key : keys) {
			if (!document.containsKey(key)) {
				throw new DocumentException(key + " value required");
			}
			k.put(key, document.get(key));
		}
		cache.put(k, document);
	}
}
