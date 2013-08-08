package net.ech.util;

import java.util.*;

/**
 * A simple LRU cache class based on LinkedHashMap.
 */
public class LruCache<K,V>
	extends LinkedHashMap<K,V>
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2092834793446869946L;
	
	private int sizeLimit;
	private Sizer<V> sizer;

	public LruCache(int sizeLimit)
	{
		this(sizeLimit, new Sizer<V>() {
			@Override
			public int getSize(V v) {
				return 1;
			}
		});
	}

	public LruCache(int sizeLimit, Sizer<V> sizer)
	{
		super(16, 0.75F, true);
		this.sizeLimit = sizeLimit;
		this.sizer = sizer;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K,V> eldest)
	{
		return getTotalSize() > sizeLimit;
	}

	private int getTotalSize()
	{
		int totalSize = 0;
		for (Map.Entry<K,V> entry : entrySet()) {
			totalSize += sizer.getSize(entry.getValue());
		}
		return totalSize;
	}
}
