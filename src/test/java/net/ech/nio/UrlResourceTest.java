package net.ech.nio;

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

public class UrlResourceTest
{
	private final static String ISO_CHAR = "iso-8859-1";

	@Test
	public void testToString() throws Exception
	{
		UrlResource.Config config = new UrlResource.Config("http://www.google.com/fantasy");
		UrlResource urlResource = new UrlResource(config);
		assertEquals("http://www.google.com/fantasy", urlResource.toString());
	}

	@Test
	public void testBadProtocol() throws Exception
	{
		UrlResource.Config config = new UrlResource.Config("looney://www.swoop.com");
		try {
			new UrlResource(config);
			fail("should not be reached");
		}
		catch (MalformedURLException e) {
			assertEquals("unknown protocol: looney", e.getMessage());
		}
	}

	@Test
	public void testEmptyPath() throws Exception
	{
		UrlResource.Config config = new UrlResource.Config("http://www.google.com");
		assertHtmlPage(config, "", ISO_CHAR, "<!doctype");
	}

	@Test
	public void testGzippedContent() throws Exception
	{
		UrlResource.Config config = new UrlResource.Config("http://en.wikipedia.org/wiki/");
		assertHtmlPage(config, "Main_Page", "utf-8", "<!DOCTYPE html>");
	}

	@Test
	public void testBasePathEndsWithSlash() throws Exception
	{
		UrlResource.Config config = new UrlResource.Config("http://www.google.com/");
		assertHtmlPage(config, "", ISO_CHAR, "<!doctype");
	}

	@Test
	public void testFileNotFoundException() throws Exception
	{
		try {
			UrlResource.Config config = new UrlResource.Config("http://www.google.com");
			UrlResource urlResource = new UrlResource(config);
			urlResource.resolve(Query.fromUriString("not-found.html"));
			fail("should not be reached");
		}
		catch (FileNotFoundException e) {
			assertEquals("http://www.google.com/not-found.html", e.getMessage());
		}
	}

	@Test
	public void testBaseQueryPreserved() throws Exception
	{
		UrlResource.Config config = new UrlResource.Config("file:src/test/resources/?a=b");
		UrlResource urlResource = new UrlResource(config);
		ItemHandle itemHandle = urlResource.resolve(Query.fromUriString("test.txt"));
		assertEquals("file:src/test/resources/test.txt?a=b", itemHandle.toString());
	}

	@Test
	public void testQueryParamsOverrideBaseParamsAndThenSome() throws Exception
	{
		UrlResource.Config config = new UrlResource.Config("file:src/test/resources/?a=b");
		UrlResource urlResource = new UrlResource(config);
		ItemHandle itemHandle = urlResource.resolve(Query.fromUriString("test.txt?a=c&d=e"));
		assertEquals("file:src/test/resources/test.txt?a=c&d=e", itemHandle.toString());
	}

	@Test
	public void testItemHandleMayOpenOnlyOnce() throws Exception
	{
		UrlResource.Config config = new UrlResource.Config("file:src/test/resources/");
		UrlResource urlResource = new UrlResource(config);
		ItemHandle itemHandle = urlResource.resolve(Query.fromUriString("test.txt"));
		Reader reader = itemHandle.openReader();
		try {
			itemHandle.openReader();
			fail("should not be reached");
		}
		catch (IllegalStateException e) {
			// Expected.
		}
	}

	private void assertHtmlPage(UrlResource.Config config, String uriString, String expectedCharacterEncoding, String expectedHead)
		throws Exception
	{
		UrlResource urlResource = new UrlResource(config);
		ItemHandle itemHandle = urlResource.resolve(Query.fromUriString(uriString));
		assertNotNull(itemHandle.getMetadata());
		assertEquals("text/html", itemHandle.getMetadata().getMimeType());
		assertEquals(expectedCharacterEncoding, itemHandle.getMetadata().getCharacterEncoding().toLowerCase());
		Reader reader = itemHandle.openReader();
		assertContentHead(reader, expectedHead);
		reader.close();
	}

	private void assertContentHead(Reader reader, String expectedHead)
		throws IOException
	{
		char[] buf = new char[expectedHead.length()];
		int cc = reader.read(buf);
		assertEquals(expectedHead.length(), cc);
		assertEquals(expectedHead, new String(buf, 0, cc));
	}
}
