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

public class BufferedContentHandleTest
{
	@Test
	public void testGetSource() throws Exception
	{
		BufferedContentHandle bch = new BufferedContentHandle("source", new JsonCodec(), new Object());
		assertEquals("source", bch.getSource());
	}

	@Test
	public void testGetSource1() throws Exception
	{
		BufferedContentHandle bch = new BufferedContentHandle("source1", new JsonCodec(), new Object());
		assertEquals("source1", bch.getSource());
	}

	@Test
	public void testGetContentType() throws Exception
	{
		BufferedContentHandle bch = new BufferedContentHandle("source", new JsonCodec(), new Object());
		assertEquals("application/json", bch.getContentType());
	}

	@Test
	public void testProxiedGetContentType() throws Exception
	{
		BufferedContentHandle bch = new BufferedContentHandle(new AbstractContentHandle(null) {
			@Override
			public Codec getCodec() {
				return new TextCodec("application/weirdness");
			}
		});
		assertEquals("application/weirdness", bch.getContentType());
	}

	@Test
	public void testGetDocument() throws Exception
	{
		BufferedContentHandle bch = new BufferedContentHandle("", new TextCodec("text/html"), "document");
		assertEquals("document", bch.getDocument());
	}

	@Test
	public void testGetDocument1() throws Exception
	{
		BufferedContentHandle bch = new BufferedContentHandle("", new TextCodec(), "document1");
		assertEquals("document1", bch.getDocument());
	}

	@Test
	public void testProxiedGetDocument() throws Exception
	{
		BufferedContentHandle bch = new BufferedContentHandle(new AbstractContentHandle(null) {
			@Override
			public Object getDocument() {
				return "document1";
			}
		});
		assertEquals("document1", bch.getDocument());
	}

	@Test
	public void testWriteJsonToOutputStream() throws Exception
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		BufferedContentHandle bch = new BufferedContentHandle("", new JsonCodec(), new Hash());
		bch.write(buffer);
		assertEquals("{}", new String(buffer.toByteArray()));
	}

	@Test
	public void testWriteTextToOutputStream() throws Exception
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		BufferedContentHandle bch = new BufferedContentHandle("", new TextCodec("text/html"), "<html></html>");
		bch.write(buffer);
		assertEquals("<html></html>", new String(buffer.toByteArray()));
	}

	@Test
	public void testWriteBytesToOutputStream() throws Exception
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] bytes = new byte[] { (byte)0, (byte)1, (byte)2 };
		BufferedContentHandle bch = new BufferedContentHandle("", new BinaryCodec("image/jpeg"), bytes);
		bch.write(buffer);
		assertTrue(Arrays.equals(bytes, buffer.toByteArray()));
	}

	@Test
	public void testWriteJsonToCharacterStream() throws Exception
	{
		StringWriter buffer = new StringWriter();
		BufferedContentHandle bch = new BufferedContentHandle("", new JsonCodec(), new Hash());
		bch.write(buffer);
		assertEquals("{}", buffer.toString());
	}

	@Test
	public void testWriteTextToCharacterStream() throws Exception
	{
		StringWriter buffer = new StringWriter();
		BufferedContentHandle bch = new BufferedContentHandle("", new TextCodec("text/html"), "<html></html>");
		bch.write(buffer);
		assertEquals("<html></html>", buffer.toString());
	}

	@Test
	public void testWriteBytesToCharacterStream() throws Exception
	{
		StringWriter buffer = new StringWriter();
		byte[] bytes = new byte[] { (byte)0, (byte)1, (byte)2 };
		BufferedContentHandle bch = new BufferedContentHandle("", new BinaryCodec("image/jpeg"), bytes);
		try {
			bch.write(buffer);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("net.ech.codec.BinaryCodec: cannot write to character output stream", e.getMessage());
		}
	}

	@Test
	public void testWriteError() throws Exception
	{
		try
		{
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			BufferedContentHandle bch = new BufferedContentHandle("", new BinaryCodec("image/jpeg"), "not really");
			bch.write(buffer);
			fail("should not be reached");
		}
		catch (IOException e)
		{
			assertEquals("Cannot write object of type java.lang.String as content type image/jpeg", e.getMessage());
		}
	}

	@Test
	public void testVersionNonNull() throws Exception
	{
		Hash hash = new Hash("abc", new Hash("def", new Hash()));
		BufferedContentHandle contentHandle = new BufferedContentHandle("", new JsonCodec(), hash);
		assertNotNull(contentHandle.getVersion());
	}

	@Test
	public void testVersionChange() throws Exception
	{
		Hash hash = new Hash("abc", new Hash("def", new Hash()));
		BufferedContentHandle contentHandle = new BufferedContentHandle("", new JsonCodec(), hash);
		String version = contentHandle.getVersion();
		hash.put("abc", "def");
		assertTrue(!version.equals(contentHandle.getVersion()));
	}
}
