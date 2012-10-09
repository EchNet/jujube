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

public class AggregateContentSourceTest
{
	AggregateContentSource aggregate;

	@Before
	public void setUp()
	{
		aggregate = new AggregateContentSource();
	}

	@Test
	public void testListStrings() throws Exception
	{
		aggregate.addChild(new MockContentSource(new String[] { "c", "b", "a" }));
		aggregate.addChild(new MockContentSource(new String[] { "d", "c", "b" }));
		aggregate.addChild(new MockContentSource(new String[] { "e", "d", "c" }));
		assertEquals(new String[] { "a", "b", "c", "d", "e" }, aggregate.list(""));
	}

	@Test
	public void testListObjects() throws Exception
	{
		aggregate.addChild(new MockContentSource(new Object[] { new Hash("id", "a") }));
		aggregate.addChild(new MockContentSource(new Object[] { "a" }));
		aggregate.addChild(new MockContentSource(new Object[] { new Hash("id", 1), new Hash() }));
		assertEquals(new Object[] { new Hash("id", 1), new Hash("id", "a") }, aggregate.list(""));
	}

	public static class MockContentSource extends AbstractContentSource
	{
		Object[] listing;

		MockContentSource(Object[] listing) {
			this.listing = listing;
		}

		@Override
		public Object[] list(String path) {
			return listing;
		}
	}
}
