package net.ech.io.template;

import net.ech.codec.*;
import net.ech.io.*;
import net.ech.util.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TemplateContentSourceTest
{
	Codec codec;
	Object document;

	@Before
	public void setUp() throws Exception
	{
		codec = new TextCodec();
		document = null;
	}

	@Test
	public void testGetSource() throws Exception
	{
		assertEquals("", resolve("").getSource());
	}

	@Test
	public void testContentType() throws Exception
	{
		codec = new TextCodec("contentType1");
		assertEquals("contentType1", resolve("").getCodec().getContentType());
	}

	@Test
	public void testProxiedGetDocument() throws Exception
	{
		document = "\"{{foo}}\"";
		assertEquals("\"{{foo}}\"", resolve("").getDocument());
	}

	@Test
	public void testEvaluatedGetDocument() throws Exception
	{
		codec = new TextCodec("application/javascript");
		document = "\"{{foo}}\"";
		assertEquals("<value>", resolve("").getDocument());
	}

	@Test
	public void testWrite() throws Exception
	{
		codec = new TextCodec("application/javascript");
		document = "\"\"";
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		resolve("").write(buffer);
		assertEquals("\"\"", new String(buffer.toByteArray()));
	}

	private ContentHandle resolve(String path)
		throws Exception
	{
		ContentSource templateContentSource = new TemplateContentSource(
			new AbstractContentSource() {
				@Override
				public ContentHandle resolve(ContentRequest request) {
					return new BufferedContentHandle(request.getPath(), codec, document);
				}
			},
			new StaticEvaluator(new TextContentHandle("<value>"))
		);
		return templateContentSource.resolve(new ContentRequest(path));
	}
}
