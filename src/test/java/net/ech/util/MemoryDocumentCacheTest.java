package net.ech.util;

import java.io.*;
import java.net.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MemoryDocumentCacheTest
{
	private MemoryDocumentCache mdCache;

	@Before
	public void setup() throws Exception
	{
		mdCache = new MemoryDocumentCache(new String[] { "id" }, 5);
	}

	@Test
	public void testInitiallyEmpty() throws Exception
	{
		assertEquals(0, mdCache.size());
	}

	@Test
	public void testPutIncreasesSize() throws Exception
	{
		mdCache.putDocument(new Hash("id", 0));
		assertEquals(1, mdCache.size());
	}

	@Test
	public void testReputDoesntIncreaseSize() throws Exception
	{
		mdCache.putDocument(new Hash("id", 0));
		mdCache.putDocument(new Hash("id", 0));
		assertEquals(1, mdCache.size());
	}

	@Test
	public void testGetDocument() throws Exception
	{
		mdCache.putDocument(new Hash("id", 0).addEntry("foo", "bar"));
		assertNotNull(mdCache.getDocument(new Hash("id", 0)));
	}

	@Test
	public void testMissingKeyOnGet() throws Exception
	{
		try
		{
			mdCache.getDocument(new Hash());
			fail("should not be reached");
		}
		catch (DocumentException e)
		{
			assertEquals("id value required", e.getMessage());
		}
	}

	@Test
	public void testMissingKeyOnPut() throws Exception
	{
		try
		{
			mdCache.putDocument(new Hash());
			fail("should not be reached");
		}
		catch (DocumentException e)
		{
			assertEquals("id value required", e.getMessage());
		}
	}

	@Test
	public void testLastPutWins() throws Exception
	{
		mdCache.putDocument(new Hash("id", 0).addEntry("foo", "bar"));
		mdCache.putDocument(new Hash("id", 0).addEntry("foo", "baz"));
		assertEquals("baz", new DQuery(mdCache.getDocument(new Hash("id", 0))).find("foo").get());
	}

	@Test
	public void testNoMoreThanLimit() throws Exception
	{
		mdCache.putDocument(new Hash("id", 0));
		mdCache.putDocument(new Hash("id", 1));
		mdCache.putDocument(new Hash("id", 2));
		mdCache.putDocument(new Hash("id", 3));
		mdCache.putDocument(new Hash("id", 4));
		mdCache.putDocument(new Hash("id", 5));
		mdCache.putDocument(new Hash("id", 6));
		assertEquals(5, mdCache.size());
    }
}
