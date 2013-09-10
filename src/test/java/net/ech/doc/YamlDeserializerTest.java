package net.ech.doc;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class YamlDeserializerTest
{
	@Test
	public void testSimple() throws Exception
	{
		YamlDeserializer deserializer = new YamlDeserializer();

		Object decoded = deserializer.deserialize(new StringReader("" + 
			"customization:\n" +
			"  config: \n" +
			"    a: b"));
        assertTrue("decodes into map", decoded instanceof Map);
		@SuppressWarnings("unchecked")
		Map<String,Object> map = (Map<String,Object>) decoded;
		assertTrue("customization", map.containsKey("customization"));
		map = (Map<String,Object>)map.get("customization");
		assertTrue("config", map.containsKey("config"));
		map = (Map<String,Object>)map.get("config");
		assertEquals("b", map.get("a"));
    }
}
