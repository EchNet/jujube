package net.ech.doc;

import org.junit.*;
import static org.junit.Assert.*;

public class RelativeDocumentResolverTest
{
	@Test
	public void testRelative() throws Exception
	{
		RelativeDocumentResolver resolver = new RelativeDocumentResolver("resource:data");
		assertEquals("A", resolver.resolve("a.json").produce().find("value").get());
		assertEquals("B", resolver.resolve("b.json").produce().find("value").get());
		assertEquals("C", resolver.resolve("c.json").produce().find("value").get());
	}

	@Test
	public void testRelativeWithExtension() throws Exception
	{
		RelativeDocumentResolver resolver = new RelativeDocumentResolver("resource:data", ".json");
		assertEquals("A", resolver.resolve("a").produce().find("value").get());
		assertEquals("B", resolver.resolve("b").produce().find("value").get());
		assertEquals("C", resolver.resolve("c").produce().find("value").get());
	}

	@Test
	public void testRelativeWithImplicitResourceBase() throws Exception
	{
		RelativeDocumentResolver resolver = new RelativeDocumentResolver(new ResourceDocumentResolver(), "data", ".json");
		assertEquals("A", resolver.resolve("a").produce().find("value").get());
		assertEquals("B", resolver.resolve("b").produce().find("value").get());
		assertEquals("C", resolver.resolve("c").produce().find("value").get());
	}

	@Test
	public void testEmptyBase() throws Exception
	{
		RelativeDocumentResolver resolver = new RelativeDocumentResolver(new ResourceDocumentResolver(), "", ".json");
		assertEquals("a", resolver.resolve("a").produce().find("value").get());
		assertEquals("b", resolver.resolve("b").produce().find("value").get());
		assertEquals("c", resolver.resolve("c").produce().find("value").get());
	}
}
