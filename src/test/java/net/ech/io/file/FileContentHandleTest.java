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

public class FileContentHandleTest
{
	@Test
	public void testVersionNotNull() throws Exception
	{
		FileContentHandle fch = new FileContentHandle(new File("src/test/resources/text.txt"), new TextCodec());
		assertNotNull(fch.getVersion());
	}

	@Test
	public void testVersionConsistent() throws Exception
	{
		String v1 = new FileContentHandle(new File("src/test/resources/text.txt"), new TextCodec()).getVersion();
		Thread.sleep(10);
		String v2 = new FileContentHandle(new File("src/test/resources/text.txt"), new TextCodec()).getVersion();
		assertEquals(v1, v2);
	}

	@Test
	public void testDefaultCacheAdvice() throws Exception
	{
		FileContentHandle fch = new FileContentHandle(new File("src/test/resources/text.txt"), new TextCodec());
		assertEquals(ContentHandle.CacheAdvice.DEFAULT, fch.getCacheAdvice());
	}
}
