package net.ech.util;

import java.util.*;
import org.junit.*;
import org.springframework.mock.web.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LruCacheTest
{
    @Test
    public void testInitial() throws Exception
    {
		LruCache<String,String> cache = new LruCache<String,String>(10);
		assertEquals("{}", cache.toString());
    }

    @Test
    public void testLRUness() throws Exception
    {
		LruCache<String,Integer> cache = makeIntCache();
		cache.put("a", 1);
		cache.put("b", 1);
		cache.put("c", 1);
		cache.get("a");
		cache.get("b");
		cache.get("b");
		cache.get("a");
		assertEquals("{c=1, b=1, a=1}", cache.toString());
    }

    @Test
    public void testDefaultSizeLimit() throws Exception
    {
		LruCache<String,Integer> cache = makeIntCache();
		cache.put("a", 1);
		cache.put("b", 1);
		cache.put("c", 1);
		cache.put("d", 1);
		cache.put("e", 1);
		cache.put("f", 1);
		assertEquals("{d=1, e=1, f=1}", cache.toString());
    }

    @Test
    public void testSizeLimit() throws Exception
    {
		LruCache<String,String> cache = makeStringCache();
		cache.put("a", "1234");
		cache.put("b", "1234");
		cache.put("c", "1234");
		cache.put("d", "1");
		cache.put("e", "1");
		cache.put("f", "1234");
		assertEquals("{c=1234, d=1, e=1, f=1234}", cache.toString());
    }

    @Test
    public void testDumpLeastRecentlyUsed() throws Exception
    {
		LruCache<String,String> cache = makeStringCache();
		cache.put("a", "1234");
		cache.put("b", "1234");
		cache.put("c", "1234");
		cache.get("a");
		cache.put("d", "1234");
		cache.put("e", "1234");
		assertEquals("{a=1234, d=1234, e=1234}", cache.toString());
    }

	private LruCache<String,Integer> makeIntCache()
	{
		return new LruCache<String,Integer>(3);
	}

	private LruCache<String,String> makeStringCache()
	{
		LruCache<String,String> cache = new LruCache<String,String>(12, new Sizer<String>() {
			public int getSize(String str) {
				return str.length();
			}
		});
		return cache;
	}
}
