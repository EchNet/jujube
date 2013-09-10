package net.ech.doc;

import org.junit.*;
import static org.junit.Assert.*;

public class DefaultDocumentResolverTest
{
	@Test
	public void testResourceDocument() throws Exception
	{
		DocumentProducer producer = new DefaultDocumentResolver().resolve("resource:a");
		Document doc = producer.produce();
		assertTrue("not null doc", !doc.isNull());
		assertEquals("a", doc.find("id").get());
	}
}
