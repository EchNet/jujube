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
	public void testPositiveBeanAsSubtype() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		IBean bean = w.pull("thing", IBean.class);
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
			w.pull("thing", IBean.class);
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
			w.pull("thing", IBean.class);
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

		Object obj = w.pull("thing");
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

		Object obj = w.pull("thing", List.class);
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

		Object obj = w.pull("thing", Bean[].class);
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
			w.pull("thing", Bean.class);
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
			w.pull("thing", List.class);
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

		Bean bean = w.pull("thing", Bean.class);
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

		Bean bean = w.pull("thing", Bean.class);
		assertNotNull(bean);
		assertEquals("{{ strings.property", bean.getProperty());
	}

	@Test
	public void testCachedObject() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla")));

		Bean bean1 = w.pull("thing", Bean.class);
		Bean bean2 = w.pull("thing", Bean.class);
		assertNotNull(bean1);
		assertEquals("manilla", bean1.getProperty());
		assertTrue("not same bean", bean1 == bean2);
	}
}
