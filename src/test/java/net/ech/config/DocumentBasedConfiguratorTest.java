package net.ech.config;

import java.io.*;
import java.util.*;
import net.ech.doc.ChildDocumentResolver;
import net.ech.doc.Document;
import net.ech.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DocumentBasedConfiguratorTest
{
	@Test
	public void testNotFoundCase() throws Exception
	{
		try {
			configure("thing", Object.class, new Hash());
		}
		catch (IOException e) {
			assertEquals("thing: no such key", e.getMessage());
		}
	}

	@Test
	public void testPositiveConfigurableClassCalledFor() throws Exception
	{
		SimpleConfigurable bean = configure("thing", SimpleConfigurable.class,
			new Hash()
				.addEntry("thing", new Hash()
					.addEntry("property", "manilla")));
		assertNotNull(bean);
		assertEquals("manilla", bean.getProperty());
	}

	@Test
	public void testPositiveConfigurableClassIdentifiedInConfig() throws Exception
	{
		Object bean = configure("thing", Object.class,
			new Hash()
				.addEntry("thing", new Hash()
					.addEntry("__type", "net.ech.config.SimpleConfigurable")
					.addEntry("property", "manilla")));

		assertNotNull(bean);
		assertTrue(bean instanceof SimpleConfigurable);
		assertEquals("manilla", ((SimpleConfigurable) bean).getProperty());
	}

	@Test
	public void testPositiveGeneric() throws Exception
	{
		Object bean = configure("thing",
			new Hash()
				.addEntry("thing", new Hash()
					.addEntry("property", "manilla")));
		assertNotNull(bean);
		assertTrue(bean instanceof Map);
		assertEquals("manilla", ((Map<String,Object>)bean).get("property"));
	}

	@Test
	public void testPositiveGenericAsObject() throws Exception
	{
		Object bean = configure("thing", Object.class,
			new Hash()
				.addEntry("thing", new Hash()
					.addEntry("property", "manilla")));
		assertNotNull(bean);
		assertTrue(bean instanceof Map);
		assertEquals("manilla", ((Map<String,Object>)bean).get("property"));
	}

	@Test
	public void testPositiveGenericAsMap() throws Exception
	{
		Map<String,Object> bean = configure("thing", Map.class,
			new Hash()
				.addEntry("thing", new Hash()
					.addEntry("property", "manilla")));
		assertNotNull(bean);
		assertEquals("manilla", bean.get("property"));
	}

	@Test
	public void testPositiveBean() throws Exception
	{
		Bean bean = configure("thing", Bean.class,
			new Hash()
				.addEntry("thing", new Hash()
					.addEntry("property", "manilla")));
		assertNotNull(bean);
		assertEquals("manilla", bean.getProperty());
	}

	@Test
	public void testPositiveBeanAsSubtype() throws Exception
	{
		IBean bean = configure("thing", IBean.class,
			new Hash()
				.addEntry("thing", new Hash()
					.addEntry("property", "manilla")));
		assertNotNull(bean);
		assertTrue(bean instanceof Bean);
		assertEquals("manilla", ((Bean)bean).getProperty());
	}

	@Test
	public void testNegativeBeanAsUnknownSubtype() throws Exception
	{
		try {
			configure("thing", IBean.class,
				new Hash()
					.addEntry("thing", new Hash()
						.addEntry("mopperty", "manilla")));
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
		try {
			configure("thing", IBean.class, 
				new Hash()
					.addEntry("thing", new Hash()
						.addEntry("property", "manilla")
						.addEntry("properly", "manilla")));
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
		Object obj = configure("thing", 
			new Hash()
				.addEntry("thing", Arrays.asList(new Object[] {
					new Hash("property", "manilla"),
					new Hash("property", "vanilla")
				})));
		assertNotNull(obj);
		assertTrue(obj instanceof List);
		assertEquals(2, ((List<?>) obj).size());
		assertTrue(((List<?>) obj).get(0) instanceof Map);
		assertTrue(((List<?>) obj).get(1) instanceof Map);
	}

	@Test
	public void testPositiveArrayAsList() throws Exception
	{
		Object obj = configure("thing", List.class,
			new Hash()
				.addEntry("thing", Arrays.asList(new Object[] {
					new Hash("property", "manilla"),
					new Hash("property", "vanilla")
				})));
		assertNotNull(obj);
		assertTrue(obj instanceof List);
		assertEquals(2, ((List<?>) obj).size());
		assertTrue(((List<?>) obj).get(0) instanceof Map);
		assertTrue(((List<?>) obj).get(1) instanceof Map);
	}

	@Test
	public void testPositiveListArrayAsArray() throws Exception
	{
		Object obj = configure("thing", Bean[].class,
			new Hash()
				.addEntry("thing", Arrays.asList(new Object[] {
					new Hash("property", "manilla"),
					new Hash("property", "vanilla")
				})));
		assertNotNull(obj);
		assertTrue(obj instanceof Bean[]);
		assertEquals(2, ((Bean[]) obj).length);
		assertTrue(((Bean[]) obj)[0] instanceof Bean);
		assertTrue(((Bean[]) obj)[1] instanceof Bean);
	}

	@Test
	public void testNegativeListArrayAsBean() throws Exception
	{
		try {
			configure("thing", Bean.class,
				new Hash()
					.addEntry("thing", Arrays.asList(new Object[] {
						new Hash("property", "manilla"),
						new Hash("property", "vanilla")
					})));
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
		try {
			configure("thing", List.class,
				new Hash()
					.addEntry("thing", new Hash()
						.addEntry("property", "manilla")));
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("cannot configure thing: java.util.List is an interface having no subtype descriptors", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testCachedObject() throws Exception
	{
		Hash hash = new Hash()
			.addEntry("thing", new Hash()
				.addEntry("property", "manilla"));
		Document doc = new Document(hash);
		DocumentBasedConfigurator w = new DocumentBasedConfigurator(doc.find("thing"), new ChildDocumentResolver(doc, ""));
		Bean bean1 = w.configure(Bean.class);
		Bean bean2 = w.configure(Bean.class);
		assertNotNull(bean1);
		assertEquals("manilla", bean1.getProperty());
		assertTrue("not same bean", bean1 == bean2);
	}

	@Test
	public void testExtends() throws Exception
	{
		Map<String,String> bean = configure("thing2", Map.class,
			new Hash()
				.addEntry("thing1", new Hash()
					.addEntry("prop1", "a")
					.addEntry("prop2", "b"))
				.addEntry("thing2", new Hash()
					.addEntry("__extends", "thing1")
					.addEntry("prop2", "bee")
					.addEntry("prop3", "c")));
		assertEquals("a", bean.get("prop1"));
		assertEquals("bee", bean.get("prop2"));
		assertEquals("c", bean.get("prop3"));
	}

	@Test
	public void testRecursiveExtends() throws Exception
	{
		Map<String,Object> bean = configure("thing2", Map.class,
			new Hash()
				.addEntry("thing1", new Hash()
					.addEntry("hash", new Hash("x", 1).addEntry("y", 2)))
				.addEntry("thing2", new Hash()
					.addEntry("__extends", "thing1")
					.addEntry("hash", new Hash("x", 0))));
		assertEquals(new Integer(0), ((Map<String,Object>)bean.get("hash")).get("x"));
	}

	@Test
	public void testContextReference() throws Exception
	{
		Map<String,Object> bean = configure("thing", Map.class,
			new Hash()
				.addEntry("thing", new Hash()
					.addEntry("__context", new Hash("x", 1))
					.addEntry("y", new Hash("__ref", "x"))));
		assertTrue(bean.containsKey("y"));
		assertEquals(new Integer(1), bean.get("y"));
	}

	private <T> T configure(String key, Class<T> clazz, Hash hash) throws Exception
	{
		Document doc = new Document(hash);
		DocumentBasedConfigurator w = new DocumentBasedConfigurator(doc.find(key), new ChildDocumentResolver(doc, ""));
		return w.configure(clazz);
	}

	private Object configure(String key, Hash hash) throws Exception
	{
		Document doc = new Document(hash);
		DocumentBasedConfigurator w = new DocumentBasedConfigurator(doc.find(key), new ChildDocumentResolver(doc, ""));
		return w.configure(Object.class);
	}
}
