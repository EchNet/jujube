package net.ech.util;

/**
 * Strategy used by LruCache to self-regulate its total size.
 */
public interface Sizer<V>
{
	public int getSize(V value);
}
