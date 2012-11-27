package net.ech.nio;

import java.io.*;
import java.net.*;
import java.util.*;
import net.ech.config.*;
import net.ech.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ResourceDescriptorTest
{
	@Test
	public void testCanTellAFileResourceConfigJustFromItsBase() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("base", "../../goo.txt")));

		Object bean = w.pull("thing", Resource.class);
		assertNotNull(bean);
		assertTrue(bean instanceof FileResource);
	}

	@Test
	public void testCanTellAUrlResourceConfigJustFromItsBase() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("base", "http://www.swoop.com/goo.txt")));

		Object bean = w.pull("thing", Resource.class);
		assertNotNull(bean);
		assertTrue(bean instanceof UrlResource);
	}

	@Test
	public void testNoBaseNoNothing() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("not_base", "http://www.swoop.com/goo.txt")));

		try {
			w.pull("thing", Resource.class);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("cannot configure thing: does not appear to configure a subtype of interface net.ech.nio.Resource", e.getMessage());
		}
	}

	@Test
	public void testBadBaseType() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("base", 6)));

		try {
			w.pull("thing", Resource.class);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("cannot configure thing: does not appear to configure a subtype of interface net.ech.nio.Resource", e.getMessage());
		}
	}
}
