package net.ech.io;

import net.ech.util.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AbstractContentHandleTest
{
	@Test
	public void testGetSource() throws Exception
	{
		assertNull(new AbstractContentHandle(null){}.getSource());
	}

	@Test
	public void testGetContentType() throws Exception
	{
		assertEquals("text/plain", new AbstractContentHandle(null){}.getContentType());
	}

	@Test
	public void testGetDocument() throws Exception
	{
		assertNull(new AbstractContentHandle(null){}.getDocument());
	}

	@Test
	public void testNoWritingToOutputStream() throws Exception
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new AbstractContentHandle(null){}.write(outputStream);
		assertTrue(Arrays.equals(new byte[0], outputStream.toByteArray()));
	}

	@Test
	public void testNoWritingToCharacterStream() throws Exception
	{
		StringWriter buf = new StringWriter();
		new AbstractContentHandle(null){}.write(buf);
		assertEquals("", buf.toString());
	}
}
