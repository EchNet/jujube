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

public class JsonpWrapperContentSourceTest
{
	Codec codec;
	Object document;
	JsonpWrapperContentSource jsonpContentSource;

	@Before
	public void setUp() throws Exception
	{
		codec = new JsonCodec();
		document = null;

		jsonpContentSource = new JsonpWrapperContentSource(new AbstractContentSource() {
			@Override
			public ContentHandle resolve(ContentRequest request) {
				return new AbstractContentHandle(request.getPath()) {
					@Override
					public Codec getCodec() {
						return codec;
					}

					@Override
					public Object getDocument() {
						return document;
					}

					@Override
					public void write(Writer writer) throws IOException {
						codec.encode(document, writer);
					}

					@Override
					public void write(OutputStream outputStream) throws IOException {
						throw new IOException("should not be called");
					}
				};
			}
		});
	}

	@Test
	public void testProxiedGetSource() throws Exception
	{
		assertEquals("source1", jsonpContentSource.resolve(new ContentRequest("source1")).getSource());
	}

	@Test
	public void testJavascriptContentType() throws Exception
	{
		assertEquals("application/x-javascript", jsonpContentSource.resolve(new ContentRequest("")).getCodec().getContentType());
	}

	@Test
	public void testGetDocument() throws Exception
	{
		assertEquals("SPX.handle(null)", jsonpContentSource.resolve(new ContentRequest("")).getDocument());
	}

	@Test
	public void testWriteDefaultCallback() throws Exception
	{
		document = "document1";
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		jsonpContentSource.resolve(new ContentRequest("")).write(buffer);
		assertEquals("SPX.handle(\"document1\")", new String(buffer.toByteArray()));
	}

	@Test
	public void testWriteOverrideCallback() throws Exception
	{
		document = new Hash();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		jsonpContentSource.resolve(new ContentRequest("", new Hash("callback", "callback1"))).write(buffer);
		assertEquals("callback1({})", new String(buffer.toByteArray()));
	}
}
