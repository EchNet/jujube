package net.ech.doc;

import java.io.IOException;
import org.junit.*;
import static org.junit.Assert.*;

public class PluginDocumentResolverTest
{
	@Test
	public void testExplicitProtocolHandler() throws Exception
	{
		PluginDocumentResolver resolver = new PluginDocumentResolver();
		resolver.addResolver("a", true, new TestProxyDocumentResolver());
		resolver.addResolver("b", false, new TestProxyDocumentResolver());
		assertEquals("a", resolver.resolve("a:a").produce().find("id").get());
		assertEquals("b", resolver.resolve("b:c").produce().find("id").get());
	}

	@Test
	public void testNoProtocolHandler() throws Exception
	{
		try {
			PluginDocumentResolver resolver = new PluginDocumentResolver();
			resolver.resolve("a:b");
			fail("should not be reached");
		}
		catch (IOException e) {
			// expected
		}
	}

	@Test
	public void testDefaultProtocolHandler() throws Exception
	{
		PluginDocumentResolver resolver = new PluginDocumentResolver();
		resolver.setDefaultProtocolResolver(new TestProxyDocumentResolver());
		assertEquals("a", resolver.resolve("a:").produce().find("id").get());
		assertEquals("b", resolver.resolve("b:").produce().find("id").get());
		assertEquals("c", resolver.resolve("c:").produce().find("id").get());
	}

	@Test
	public void testDefaultHandler() throws Exception
	{
		PluginDocumentResolver resolver = new PluginDocumentResolver();
		resolver.setDefaultResolver(new TestProxyDocumentResolver());
		assertEquals("a", resolver.resolve("apple").produce().find("id").get());
		assertEquals("b", resolver.resolve("banana").produce().find("id").get());
		assertEquals("c", resolver.resolve("coconut").produce().find("id").get());
	}

	@Test
	public void testNoHandler() throws Exception
	{
		try {
			PluginDocumentResolver resolver = new PluginDocumentResolver();
			resolver.resolve("a");
			fail("should not be reached");
		}
		catch (IOException e) {
			// expected
		}
	}

	private static class TestProxyDocumentResolver extends ProxyDocumentResolver
	{
		TestProxyDocumentResolver()
		{
			super(new ResourceDocumentResolver());
		}

		@Override
		public String mutateDocumentKey(String key)
		{
			return key.substring(0, 1) + ".json";
		}
	}
}
