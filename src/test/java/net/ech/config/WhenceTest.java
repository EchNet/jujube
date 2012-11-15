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
	public void testNullCase() throws Exception
	{
		Whence w = new Whence(new Hash());
		SimpleConfigurable bean = w.pull("thing", SimpleConfigurable.class);
		assertNull(bean);
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
			assertEquals("cannot configure thing", e.getMessage());
			assertNotNull(e.getCause());
			assertEquals("java.util.List: is an interface", e.getCause().getMessage());
		}
	}

	public static class Bean
	{
		private String property;
		public String getProperty() { return property; }
		public void setProperty(String property) { this.property = property; }
	}
}
