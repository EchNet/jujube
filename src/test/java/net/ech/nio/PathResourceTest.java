package net.ech.nio;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PathResourceTest
{
	@Test
	public void testEmptyPathError() throws Exception
	{
		try
		{
			new PathResource(new PathResource.Config()).resolve(Query.fromUriString("test.txt"));
			fail("should not be reached");
		}
		catch (IOException e)
		{
			assertEquals("empty source path", e.getMessage());
		}
	}

	@Test
	public void testToString() throws Exception
	{
		PathResource.Config pConfig = new PathResource.Config();
		pConfig.addResource(new FileResource(new FileResource.Config("a")));
		pConfig.addResource(new FileResource(new FileResource.Config("b")));
		PathResource pSource = new PathResource(pConfig);
		assertEquals("[a:b]", pSource.toString());
	}

	@Test
	public void testPathSecondComponent() throws Exception
	{
		PathResource.Config pConfig = new PathResource.Config();
		pConfig.addResource(new FileResource(new FileResource.Config("not/there/")));
		pConfig.addResource(new FileResource(new FileResource.Config("src/test/resources/")));
		pConfig.addResource(new FileResource(new FileResource.Config("not/there/either/")));
		PathResource pSource = new PathResource(pConfig);
		assertEquals("src/test/resources/test.txt", pSource.resolve(Query.fromUriString("test.txt")).toString());
	}

	@Test
	public void testPathAllDuds() throws Exception
	{
		try
		{
			PathResource.Config pConfig = new PathResource.Config();
			pConfig.addResource(new FileResource(new FileResource.Config("not/there/")));
			pConfig.addResource(new FileResource(new FileResource.Config("not/there/either/")));
			PathResource pSource = new PathResource(pConfig);
			pSource.resolve(Query.fromUriString("test.txt"));
			fail("should not be reached");
		}
		catch (FileNotFoundException e)
		{
			assertEquals("test.txt", e.getMessage());
		}
	}
}
