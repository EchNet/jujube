package net.ech.io;

import net.ech.io.file.*;
import net.ech.util.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PathContentSourceTest
{
	@Test
	public void testPathSecondComponent() throws Exception
	{
		PathContentSource pSource = new PathContentSource();
		pSource.addContentSource(new UrlContentSource(new URL("file:not/there/")));
		pSource.addContentSource(new UrlContentSource(new URL("file:src/test/resources/")));
		pSource.addContentSource(new UrlContentSource(new URL("file:not/there/either/")));
		assertEquals("file:src/test/resources/test.txt", ((UrlContentHandle) pSource.resolve(new ContentRequest("test.txt"))).getUrl().toString());
	}

	@Test
	public void testPathAllDuds() throws Exception
	{
		try
		{
			PathContentSource pSource = new PathContentSource();
			pSource.addContentSource(new UrlContentSource(new URL("file:not/there/")));
			pSource.addContentSource(new UrlContentSource(new URL("file:not/there/either/")));
			pSource.resolve(new ContentRequest("test.txt"));
			fail("should not be reached");
		}
		catch (FileNotFoundException e)
		{
			assertEquals("test.txt: not found in [file:not/there/, file:not/there/either/]", e.getMessage());
		}
	}

	@Test
	public void testEmptyPathError() throws Exception
	{
		try
		{
			new PathContentSource().resolve(new ContentRequest("test.txt"));
			fail("should not be reached");
		}
		catch (IOException e)
		{
			assertEquals("empty source path", e.getMessage());
		}
	}

	@Test
	public void testList() throws Exception
	{
		PathContentSource pSource = new PathContentSource();
		pSource.addChild(new FileContentSource(new File("src/test")));
		pSource.addChild(new FileContentSource(new File("src/test/java")));
		assertEquals(new String[] { "java", "net", "resources" }, pSource.list(""));
	}
}
