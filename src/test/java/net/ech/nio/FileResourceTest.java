package net.ech.nio;

import java.io.*;
import java.net.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FileResourceTest
{
	@Test
	public void testPlainTextFile() throws Exception
	{
		testPlainTextFile("test.txt");
	}

	@Test
	public void testUriComponentsOtherThanPathIgnored() throws Exception
	{
		testPlainTextFile("file://auth/test.txt#ignored");
	}

	private void testPlainTextFile(String uriString) throws Exception
	{
		Resource resource = getFileResource();
		Query query = new Query(uriString);
		ItemHandle itemHandle = resource.resolve(query);
		assertEquals("src/test/resources/" + query.getPath(), itemHandle.toString());
		assertTrue(itemHandle.isLatent());
		assertNotNull(itemHandle.getMetadata());
		assertEquals("text/plain", itemHandle.getMetadata().getMimeType());
		assertEquals("UTF-8", itemHandle.getMetadata().getCharacterEncoding());
		Reader reader = itemHandle.presentReader();
		assertNotNull(reader);
		char[] buf = new char[100];
		int cc = reader.read(buf);
		assertEquals("abc\n", new String(buf, 0, cc));
	}

	@Test
	public void testFileNotFound() throws Exception
	{
		try {
			Resource resource = getFileResource();
			Query query = new Query("not_found.txt");
			resource.resolve(query);
			fail("should not be reached");
		}
		catch (FileNotFoundException e) {
			// Expected.
		}
	}

	@Test
	public void testDefaultContentType() throws Exception
	{
		ItemHandle itemHandle = getFileResource().resolve(new Query("test.txt"));
		assertEquals("text/plain", itemHandle.getMetadata().getMimeType());
	}

	@Test
	public void testJsonContentType() throws Exception
	{
		ItemHandle itemHandle = getFileResource().resolve(new Query("test.json"));
		assertEquals("application/json", itemHandle.getMetadata().getMimeType());
	}

	@Test
	public void testJavascriptContentType() throws Exception
	{
		ItemHandle itemHandle = getFileResource().resolve(new Query("test.js"));
		assertEquals("application/x-javascript", itemHandle.getMetadata().getMimeType());
	}

	@Test
	public void testCssContentType() throws Exception
	{
		ItemHandle itemHandle = getFileResource().resolve(new Query("test.css"));
		assertEquals("text/css", itemHandle.getMetadata().getMimeType());
	}

	@Test
	public void testGifContentType() throws Exception
	{
		ItemHandle itemHandle = getFileResource().resolve(new Query("test.gif"));
		assertEquals("image/gif", itemHandle.getMetadata().getMimeType());
	}

	@Test
	public void testPngContentType() throws Exception
	{
		ItemHandle itemHandle = getFileResource().resolve(new Query("test.png"));
		assertEquals("image/png", itemHandle.getMetadata().getMimeType());
	}
	
	private Resource getFileResource()
	{
		return new FileResource(new FileResource.Config(new File("src/test/resources/")));
	}
}
