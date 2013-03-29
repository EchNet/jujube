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
			w.configure("thing", Object.class);
		}
		catch (IOException e) {
			assertEquals("thing: no such key", e.getMessage());
		}
	}

	@Test
	public void testPositiveConfigurableClassCalledFor() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		SimpleConfigurable bean = w.configure("thing", SimpleConfigurable.class);
		assertNotNull(bean);
		assertEquals("manilla", bean.getProperty());
	}

	@Test
	public void testPositiveConfigurableClassIdentifiedInConfig() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("_type", "net.ech.config.SimpleConfigurable")
				.addEntry("property", "manilla")));

		Object bean = w.configure("thing", Object.class);
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

		Object bean = w.configure("thing");
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

		Object bean = w.configure("thing", Object.class);
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

		Map<String,Object> bean = w.configure("thing", Map.class);
		assertNotNull(bean);
		assertEquals("manilla", bean.get("property"));
	}

	@Test
	public void testPositiveBean() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		Bean bean = w.configure("thing", Bean.class);
		assertNotNull(bean);
		assertEquals("manilla", bean.getProperty());
	}

	@Test
	public void testPositiveBeanAsSubtype() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		IBean bean = w.configure("thing", IBean.class);
		assertNotNull(bean);
		assertTrue(bean instanceof Bean);
		assertEquals("manilla", ((Bean)bean).getProperty());
	}

	@Test
	public void testNegativeBeanAsUnknownSubtype() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("mopperty", "manilla")));

		try {
			w.configure("thing", IBean.class);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("cannot configure thing: does not appear to configure a subtype of interface net.ech.config.IBean", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testNegativeBeanAsAmbiguousSubtype() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")
				.addEntry("properly", "manilla")));

		try {
			w.configure("thing", IBean.class);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("cannot configure thing: ambiguous subtype", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testPositiveArrayAsObject() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", Arrays.asList(new Object[] {
				new Hash("property", "manilla"),
				new Hash("property", "vanilla")
			})));

		Object obj = w.configure("thing");
		assertNotNull(obj);
		assertTrue(obj instanceof List);
		assertEquals(2, ((List<?>) obj).size());
		assertTrue(((List<?>) obj).get(0) instanceof Map);
		assertTrue(((List<?>) obj).get(1) instanceof Map);
	}

	@Test
	public void testPositiveArrayAsList() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", Arrays.asList(new Object[] {
				new Hash("property", "manilla"),
				new Hash("property", "vanilla")
			})));

		Object obj = w.configure("thing", List.class);
		assertNotNull(obj);
		assertTrue(obj instanceof List);
		assertEquals(2, ((List<?>) obj).size());
		assertTrue(((List<?>) obj).get(0) instanceof Map);
		assertTrue(((List<?>) obj).get(1) instanceof Map);
	}

	@Test
	public void testPositiveListArrayAsArray() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", Arrays.asList(new Object[] {
				new Hash("property", "manilla"),
				new Hash("property", "vanilla")
			})));

		Object obj = w.configure("thing", Bean[].class);
		assertNotNull(obj);
		assertTrue(obj instanceof Bean[]);
		assertEquals(2, ((Bean[]) obj).length);
		assertTrue(((Bean[]) obj)[0] instanceof Bean);
		assertTrue(((Bean[]) obj)[1] instanceof Bean);
	}

	@Test
	public void testNegativeListArrayAsBean() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", Arrays.asList(new Object[] {
				new Hash("property", "manilla"),
				new Hash("property", "vanilla")
			})));

		try {
			w.configure("thing", Bean.class);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("cannot configure thing: class net.ech.config.Bean cannot be configured with an array", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testNegativeInterface() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		try {
			w.configure("thing", List.class);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("cannot configure thing: java.util.List is an interface having no subtype descriptors", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testSimpleReference() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "{{ strings.property }}"))
			.addEntry("strings", new Hash()
				.addEntry("property", "manilla")));

		Bean bean = w.configure("thing", Bean.class);
		assertNotNull(bean);
		assertEquals("manilla", bean.getProperty());
	}

	@Test
	public void testMisformedReference() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "{{ strings.property"))
			.addEntry("strings", new Hash()
				.addEntry("property", "manilla")));

		Bean bean = w.configure("thing", Bean.class);
		assertNotNull(bean);
		assertEquals("{{ strings.property", bean.getProperty());
	}

	@Test
	public void testCachedObject() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		Bean bean1 = w.configure("thing", Bean.class);
		Bean bean2 = w.configure("thing", Bean.class);
		assertNotNull(bean1);
		assertEquals("manilla", bean1.getProperty());
		assertTrue("not same bean", bean1 == bean2);
	}

	@Test
	public void testExtends() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing1", new Hash()
				.addEntry("prop1", "a")
				.addEntry("prop2", "b"))
			.addEntry("thing2", new Hash()
				.addEntry("_extends", "thing1")
				.addEntry("prop2", "bee")
				.addEntry("prop3", "c")));
		Map<String,String> bean = w.configure("thing2", Map.class);
		assertEquals("a", bean.get("prop1"));
		assertEquals("bee", bean.get("prop2"));
		assertEquals("c", bean.get("prop3"));
	}

	@Test
	public void testRecursiveExtends() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing1", new Hash()
				.addEntry("hash", new Hash("x", 1).addEntry("y", 2)))
			.addEntry("thing2", new Hash()
				.addEntry("_extends", "thing1")
				.addEntry("hash", new Hash("x", 0))));
		Map<String,Object> bean = w.configure("thing2", Map.class);
		assertEquals(new Integer(0), ((Map<String,Object>)bean.get("hash")).get("x"));
	}
}
