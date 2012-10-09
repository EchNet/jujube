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

public class AbstractContentSourceTest
{
	@Test
	public void testDefaultImpl() throws Exception
	{
		try
		{
			new AbstractContentSource(){}.resolve(new ContentRequest(""));
			fail("should not be reached");
		}
		catch (IOException e)
		{
			assertTrue(e.getMessage().endsWith("resolve() not implemented"));
		}
	}

	@Test
	public void testResolveNoParamsCallsResolvePrime() throws Exception
	{
		assertTrue(new RiggedContentSource().resolve(new ContentRequest("")) instanceof RiggedContentHandle);
	}

	@Test
	public void testResolveNoParamsPassesPathThru() throws Exception
	{
		assertEquals("hi", ((RiggedContentHandle) new RiggedContentSource().resolve(new ContentRequest("hi"))).path);
	}

	@Test
	public void testResolveNoParamsPassesEmptyMap() throws Exception
	{
		assertEquals(new Hash(), new RiggedContentSource().resolve(new ContentRequest("hi")).getDocument());
	}

	@Test
	public void testList() throws Exception
	{
		try
		{
			new AbstractContentSource(){}.list("foo");
			fail("should not be reached");
		}
		catch (IOException e)
		{
			assertTrue(e.getMessage().endsWith(": list() not implemented"));
		}
	}

	private class RiggedContentSource extends AbstractContentSource
	{
		@Override
		public ContentHandle resolve(ContentRequest request) {
			return new RiggedContentHandle(request.getPath(), request.getParameters());
		}

		ContentHandle resolve(String path) {
			return resolve(new ContentRequest(path));
		}
	}

	private class RiggedContentHandle extends AbstractContentHandle
	{
		String path;
		Object document;

		public RiggedContentHandle(String path, Object document)
		{
			super(path);
			this.path = path;
			this.document = document;
		}

		@Override
		public Object getDocument()
		{
			return document;
		}
	}
}
