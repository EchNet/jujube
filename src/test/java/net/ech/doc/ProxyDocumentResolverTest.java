package net.ech.doc;

import org.junit.*;
import static org.junit.Assert.*;

public class ProxyDocumentResolverTest
{
	@Test
	public void testProxyDocumentResolver() throws Exception
	{
		assertEquals("a", new TestProxyDocumentResolver().resolve("apple").produce().find("id").get());
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
