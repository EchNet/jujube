package net.ech.doc;

import org.junit.*;
import static org.junit.Assert.*;

public class ResourceDocumentResolverTest
{
	@Test
	public void testResourceDocumentResolver() throws Exception
	{
		assertEquals("a", new ResourceDocumentResolver().resolve("a").produce().find("id").get());
	}
}
