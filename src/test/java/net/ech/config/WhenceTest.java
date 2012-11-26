package net.ech.config;

import java.io.*;
import java.util.*;
import net.ech.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WhenceTest
{
	@Test
	public void testNotFoundCase() throws Exception
	{
		try {
			Whence w = new Whence(new Hash());
			w.pull("thing", Object.class);
		}
		catch (IOException e) {
			assertEquals("cannot configure thing: no such key", e.getMessage());
		}
	}

	@Test
	public void testPositiveConfigurableClassCalledFor() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		SimpleConfigurable bean = w.pull("thing", SimpleConfigurable.class);
		assertNotNull(bean);
		assertEquals("manilla", bean.getProperty());
	}

	@Test
	public void testPositiveConfigurableClassIdentifiedInConfig() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("$class", "net.ech.config.SimpleConfigurable")
				.addEntry("property", "manilla")));

		Object bean = w.pull("thing", Object.class);
		assertNotNull(bean);
		assertTrue(bean instanceof SimpleConfigurable);
		assertEquals("manilla", ((SimpleConfigurable) bean).getProperty());
	}

	@Test
	public void testPositiveGeneric() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		Object bean = w.pull("thing");
		assertNotNull(bean);
		assertTrue(bean instanceof Map);
		assertEquals("manilla", ((Map<String,Object>)bean).get("property"));
	}

	@Test
	public void testPositiveGenericAsObject() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		Object bean = w.pull("thing", Object.class);
		assertNotNull(bean);
		assertTrue(bean instanceof Map);
		assertEquals("manilla", ((Map<String,Object>)bean).get("property"));
	}

	@Test
	public void testPositiveGenericAsMap() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		Map<String,Object> bean = w.pull("thing", Map.class);
		assertNotNull(bean);
		assertEquals("manilla", bean.get("property"));
	}

	@Test
	public void testPositiveBean() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		Bean bean = w.pull("thing", Bean.class);
		assertNotNull(bean);
		assertEquals("manilla", bean.getProperty());
	}

	@Test
	public void testPositiveListGenericElement() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", Arrays.asList(new Object[] {
				new Hash("property", "manilla"),
				new Hash("property", "vanilla")
			})));

		Object obj = w.pull("thing");
		assertNotNull(obj);
		assertTrue(obj instanceof List);
		assertEquals(2, ((List<?>) obj).size());
		assertTrue(((List<?>) obj).get(0) instanceof Map);
		assertTrue(((List<?>) obj).get(1) instanceof Map);
	}

	@Test
	public void testPositiveListElementTypeGiven() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", Arrays.asList(new Object[] {
				new Hash("property", "manilla"),
				new Hash("property", "vanilla")
			})));

		Object obj = w.pull("thing", List.class);
		assertNotNull(obj);
		assertTrue(obj instanceof List);
		assertEquals(2, ((List<?>) obj).size());
		assertTrue(((List<?>) obj).get(0) instanceof Map);
		assertTrue(((List<?>) obj).get(1) instanceof Map);
	}

	@Test
	public void testPositiveListArrayElementTypeGiven() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", Arrays.asList(new Object[] {
				new Hash("property", "manilla"),
				new Hash("property", "vanilla")
			})));

		Object obj = w.pull("thing", Bean[].class);
		assertNotNull(obj);
		assertTrue(obj instanceof Bean[]);
		assertEquals(2, ((Bean[]) obj).length);
		assertTrue(((Bean[]) obj)[0] instanceof Bean);
		assertTrue(((Bean[]) obj)[1] instanceof Bean);
	}

	@Test
	public void testNegativeInterface() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		try {
			w.pull("thing", List.class);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("cannot configure thing: java.util.List is an interface", e.getMessage());
			assertNotNull(e.getCause());
			assertEquals("java.util.List is an interface", e.getCause().getMessage());
		}
	}

	/****
	@Test
	public void testSimpleReference() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "{{ strings.property }}"))
			.addEntry("strings", new Hash()
				.addEntry("property", "manilla")));

		Bean bean = w.pull("thing", Bean.class);
		assertNotNull(bean);
		assertEquals("manilla", bean.getProperty());
	}
	****/
}
