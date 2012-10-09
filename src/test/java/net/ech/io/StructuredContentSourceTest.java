package net.ech.io;

import net.ech.codec.*;
import net.ech.util.*;
import net.ech.service.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StructuredContentSourceTest
{
	StructuredContentSource contentSource;

	@Before
	public void setUp() throws Exception
	{
		contentSource = new StructuredContentSource(Collections.singletonMap("a", (ContentSource) new MetaWrapperContentSource(new NullContentSource())));
	}

	@Test
	public void testEmptyPath() throws Exception
	{
		try {
			resolve("");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals(": access denied", e.getMessage());
		}
	}

	@Test
	public void testNearlyEmptyPath() throws Exception
	{
		try {
			resolve("/");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("/: access denied", e.getMessage());
		}
	}

	@Test
	public void testBadComponent() throws Exception
	{
		try {
			resolve("/b");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("/b: not found", e.getMessage());
		}
	}

	@Test
	public void testSpotOn() throws Exception
	{
		assertEquals(new Hash(), resolve("/a"));
	}

	@Test
	public void testExtraPath() throws Exception
	{
		assertEquals(new Hash("path", "c").addEntry("source", "c"), resolve("/a/c"));
	}

	@Test
	public void testMoreExtraPath() throws Exception
	{
		assertEquals(new Hash("path", "c/d").addEntry("source", "c/d"), resolve("/a/c/d"));
	}

	private Object resolve(String path)
		throws Exception
	{
		return contentSource.resolve(new ContentRequest(path)).getDocument();
	}
}
