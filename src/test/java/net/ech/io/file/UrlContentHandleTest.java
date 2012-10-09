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

public class UrlContentHandleTest
{
	@Test
	public void testConstructorError() throws Exception
	{
		try {
			URL url = new URL("file:not/there/test.txt");
			new UrlContentHandle(url);
			fail("should not be reached");
		}
		catch (FileNotFoundException e) {
			assertEquals("not/there/test.txt (No such file or directory)", e.getMessage());
		}
	}

	@Test
	public void testGetUrl() throws Exception
	{
		URL url = new URL("file:src/test/resources/test.txt");
		UrlContentHandle bch = new UrlContentHandle(url);
		assertEquals(url, bch.getUrl());
	}

	@Test
	public void testGetSource() throws Exception
	{
		UrlContentHandle bch = new UrlContentHandle(new URL("file:src/test/resources/test.txt"));
		assertEquals("file:src/test/resources/test.txt", bch.getSource());
	}

	@Test
	public void testGetContentType() throws Exception
	{
		UrlContentHandle bch = new UrlContentHandle(new URL("file:src/test/resources/test.txt"));
		assertEquals("text/plain", bch.getContentType());
	}

	@Test
	public void testOverrideContentType() throws Exception
	{
		UrlContentHandle bch = new UrlContentHandle(new URL("file:src/test/resources/test.txt"), new TextCodec("text/html"));
		assertEquals("text/html", bch.getContentType());
	}

	@Test
	public void testGetJsonDocument() throws Exception
	{
		UrlContentHandle bch = new UrlContentHandle(new URL("file:src/test/resources/test.json"), new JsonCodec());
		assertTrue(bch.getDocument() instanceof Map);
	}

	@Test
	public void testGetTextDocument() throws Exception
	{
		UrlContentHandle bch = new UrlContentHandle(new URL("file:src/test/resources/test.txt"));
		assertEquals("abc\n", bch.getDocument());
	}

	@Test
	public void testGetBinaryDocument() throws Exception
	{
		UrlContentHandle bch = new UrlContentHandle(new URL("file:src/test/resources/test.txt"), new BinaryCodec("image/jpeg"));
		assertTrue(bch.getDocument() instanceof byte[]);
	}

	@Test
	public void testWriteTextToOutputStream() throws Exception
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		UrlContentHandle bch = new UrlContentHandle(new URL("file:src/test/resources/test.txt"));
		bch.write(buffer);
		assertEquals("abc\n", new String(buffer.toByteArray()));
	}

	@Test
	public void testWriteBytesToOutputStream() throws Exception
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		UrlContentHandle bch = new UrlContentHandle(new URL("file:src/test/resources/test.txt"), new BinaryCodec("image/jpeg"));
		bch.write(buffer);
		assertEquals("abc\n", new String(buffer.toByteArray()));
	}

	@Test
	public void testWriteTextToCharacterStream() throws Exception
	{
		StringWriter buffer = new StringWriter();
		UrlContentHandle bch = new UrlContentHandle(new URL("file:src/test/resources/test.txt"));
		bch.write(buffer);
		assertEquals("abc\n", new String(buffer.toString()));
	}

	@Test
	public void testWriteBytesToCharacterStream() throws Exception
	{
		StringWriter buffer = new StringWriter();
		UrlContentHandle bch = new UrlContentHandle(new URL("file:src/test/resources/test.txt"), new BinaryCodec("image/jpeg"));
		try {
			bch.write(buffer);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("net.ech.codec.BinaryCodec: cannot write to character output stream", e.getMessage());
		}
	}

	@Test
	public void testFindCodec() throws Exception
	{
		assertEquals("text/html", UrlContentHandle.findCodec("text/html").getContentType());
	}

	@Test
	public void testFindCodecDefaultCharSet() throws Exception
	{
		assertEquals("UTF-8", UrlContentHandle.findCodec("text/html").getCharacterEncoding());
	}

	@Test
	public void testFindCodecSpecifiedCharSet() throws Exception
	{
		assertEquals("ISO-8859-1", UrlContentHandle.findCodec("text/html; charset=ISO-8859-1").getCharacterEncoding());
	}

	@Test
	public void testFindCodecSpecifiedCharSet1() throws Exception
	{
		assertEquals("ISO-8859-1", UrlContentHandle.findCodec("text/html; charset=ISO-8859-1; extraneous").getCharacterEncoding());
	}
}
