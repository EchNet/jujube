package net.ech.io;

import net.ech.codec.*;
import net.ech.util.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ContentTypesTest
{
	// For line coverage!
	private ContentTypes _dummy_ = new ContentTypes(){};

	@Test
	public void testTextPlainIsText() throws Exception
	{
		assertTrue(ContentTypes.isText("text/plain"));
	}

	@Test
	public void testTextPlainIsntJson() throws Exception
	{
		assertTrue(!ContentTypes.isJson("text/plain"));
	}

	@Test
	public void testTextPlainIsntJavascript() throws Exception
	{
		assertTrue(!ContentTypes.isJavascript("text/plain"));
	}

	@Test
	public void testTextHtmlIsText() throws Exception
	{
		assertTrue(ContentTypes.isText("text/html"));
	}

	@Test
	public void testTextHtmlIsntJson() throws Exception
	{
		assertTrue(!ContentTypes.isJson("text/html"));
	}

	@Test
	public void testTextHtmlIsntJavascript() throws Exception
	{
		assertTrue(!ContentTypes.isJavascript("text/html"));
	}

	@Test
	public void testJsonIsText() throws Exception
	{
		assertTrue(ContentTypes.isText("application/json"));
	}

	@Test
	public void testJsonIsJson() throws Exception
	{
		assertTrue(ContentTypes.isJson("application/json"));
	}

	@Test
	public void testJsonIsntJavascript() throws Exception
	{
		assertTrue(!ContentTypes.isJavascript("application/json"));
	}

	@Test
	public void testJavascriptIsText() throws Exception
	{
		assertTrue(ContentTypes.isText("application/javascript"));
	}

	@Test
	public void testJavascriptIsntJson() throws Exception
	{
		assertTrue(!ContentTypes.isJson("application/javascript"));
	}

	@Test
	public void testJavascriptIsJavascript() throws Exception
	{
		assertTrue(ContentTypes.isJavascript("application/javascript"));
	}

	@Test
	public void testXJavascriptIsText() throws Exception
	{
		assertTrue(ContentTypes.isText("application/x-javascript"));
	}

	@Test
	public void testXJavascriptIsntJson() throws Exception
	{
		assertTrue(!ContentTypes.isJson("application/x-javascript"));
	}

	@Test
	public void testXJavascriptIsJavascript() throws Exception
	{
		assertTrue(ContentTypes.isJavascript("application/x-javascript"));
	}

	@Test
	public void testFormIsText() throws Exception
	{
		assertTrue(ContentTypes.isText(ContentTypes.FORM_CONTENT_TYPE));
	}

	@Test
	public void testCsvCodecType() throws Exception
	{
		assertTrue(ContentTypes.getDefaultCodec("text/csv") instanceof CsvCodec);
	}

	@Test
	public void testCsvCodecDefaultCharEncoding() throws Exception
	{
		assertEquals("UTF-8", ContentTypes.getDefaultCodec("text/csv").getCharacterEncoding());
	}

	@Test
	public void testCsvCodecSpecificCharEncoding() throws Exception
	{
		assertEquals("NONO", ContentTypes.getDefaultCodec("text/csv", "NONO").getCharacterEncoding());
	}

	@Test
	public void testJsonCodecType() throws Exception
	{
		assertTrue(ContentTypes.getDefaultCodec("application/json") instanceof JsonCodec);
	}

	@Test
	public void testJsonCodecDefaultCharEncoding() throws Exception
	{
		assertEquals("UTF-8", ContentTypes.getDefaultCodec("application/json").getCharacterEncoding());
	}

	@Test
	public void testJsonCodecSpecificCharEncoding() throws Exception
	{
		assertEquals("NONO", ContentTypes.getDefaultCodec("application/json", "NONO").getCharacterEncoding());
	}

	@Test
	public void testHtmlCodecType() throws Exception
	{
		assertTrue(ContentTypes.getDefaultCodec("text/html", null) instanceof TextCodec);
	}

	@Test
	public void testHtmlCodecContentType() throws Exception
	{
		assertEquals("text/html", ContentTypes.getDefaultCodec("text/html").getContentType());
	}

	@Test
	public void testHtmlCodecDefaultCharEncoding() throws Exception
	{
		assertEquals("UTF-8", ContentTypes.getDefaultCodec("text/html").getCharacterEncoding());
	}

	@Test
	public void testHtmlCodecSpecificCharEncoding() throws Exception
	{
		assertEquals("NONO", ContentTypes.getDefaultCodec("text/html", "NONO").getCharacterEncoding());
	}

	@Test
	public void testBinaryCodecType() throws Exception
	{
		assertTrue(ContentTypes.getDefaultCodec("image/jpeg") instanceof BinaryCodec);
	}

	@Test
	public void testBinaryCodecContentType() throws Exception
	{
		assertEquals("image/jpeg", ContentTypes.getDefaultCodec("image/jpeg").getContentType());
	}

	@Test
	public void testBinaryCodecDefaultCharEncoding() throws Exception
	{
		assertNull(ContentTypes.getDefaultCodec("image/jpeg").getCharacterEncoding());
	}

	@Test
	public void testBinaryCodecSpecificCharEncodingIgnored() throws Exception
	{
		assertNull(ContentTypes.getDefaultCodec("image/jpeg", "NONO").getCharacterEncoding());
	}
}
