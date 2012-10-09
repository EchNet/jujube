package net.ech.io.file;

import net.ech.codec.*;
import net.ech.io.*;
import net.ech.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FileContentSourceTest
{
	@Test
	public void testFileNotFound() throws Exception
	{
		try {
			new FileContentSource(new File("src/test/resources/")).resolve(new ContentRequest("text.txt"));
			fail("should not be reached");
		}
		catch (FileNotFoundException e) {
			// Expected.
		}
	}

	@Test
	public void testDefaultContentType() throws Exception
	{
		ContentHandle fch = new FileContentSource(new File("src/test/resources/")).resolve(new ContentRequest("test.txt"));
		assertEquals("text/plain", fch.getCodec().getContentType());
	}

	@Test
	public void testJsonContentType() throws Exception
	{
		ContentHandle fch = new FileContentSource(new File("src/test/resources/")).resolve(new ContentRequest("test.json"));
		assertEquals("application/json", fch.getCodec().getContentType());
	}

	@Test
	public void testJavascriptContentType() throws Exception
	{
		ContentHandle fch = new FileContentSource(new File("src/test/resources/")).resolve(new ContentRequest("test.js"));
		assertEquals("application/x-javascript", fch.getCodec().getContentType());
	}

	@Test
	public void testCssContentType() throws Exception
	{
		ContentHandle fch = new FileContentSource(new File("src/test/resources/")).resolve(new ContentRequest("test.css"));
		assertEquals("text/css", fch.getCodec().getContentType());
	}

	@Test
	public void testGifContentType() throws Exception
	{
		ContentHandle fch = new FileContentSource(new File("src/test/resources/")).resolve(new ContentRequest("test.gif"));
		assertEquals("image/gif", fch.getCodec().getContentType());
	}

	@Test
	public void testPngContentType() throws Exception
	{
		ContentHandle fch = new FileContentSource(new File("src/test/resources/")).resolve(new ContentRequest("test.png"));
		assertEquals("image/png", fch.getCodec().getContentType());
	}

	@Test
	public void testDevConfigDocument() throws Exception
	{
		ContentHandle devJson = new FileContentSource(new File("config")).resolve(new ContentRequest("dev.json"));
		assertTrue(devJson.getDocument() instanceof Map);
	}

	@Test
	public void testSource() throws Exception
	{
		ContentHandle devJson = new FileContentSource(new File("config")).resolve(new ContentRequest("dev.json"));
		assertEquals("dev.json", devJson.getSource());
	}

	@Test
	public void testIgnoresAuthority() throws Exception
	{
		ContentHandle devJson = new FileContentSource(new File("config")).resolve(new ContentRequest("//auth/dev.json"));
		assertEquals("//auth/dev.json", devJson.getSource());
	}

	@Test
	public void testDefaultCacheAdvice() throws Exception
	{
		ContentHandle fch = new FileContentSource(new File("src/test/resources/")).resolve(new ContentRequest("test.txt"));
		assertEquals(ContentHandle.CacheAdvice.DEFAULT, fch.getCacheAdvice());
	}

	@Test
	public void testDefaultCacheAdvice2() throws Exception
	{
		ContentHandle fch = new FileContentSource(new File("src/test/resources/"), false).resolve(new ContentRequest("test.txt"));
		assertEquals(ContentHandle.CacheAdvice.DEFAULT, fch.getCacheAdvice());
	}

	@Test
	public void testSetStaticSourceSetsIndefiniteCache() throws Exception
	{
		ContentHandle fch = new FileContentSource(new File("src/test/resources/"), true).resolve(new ContentRequest("test.txt"));
		assertEquals(ContentHandle.CacheAdvice.CACHE_INDEFINITELY, fch.getCacheAdvice());
	}

	@Test
	public void testSetStaticSourceSetsIndefiniteCache2() throws Exception
	{
		FileContentSource fcs = new FileContentSource(new File("src/test/resources/"));
		fcs.setStatic(true);
		ContentHandle fch = fcs.resolve(new ContentRequest("test.txt"));
		assertEquals(ContentHandle.CacheAdvice.CACHE_INDEFINITELY, fch.getCacheAdvice());
	}
}
