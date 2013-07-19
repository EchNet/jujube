package net.ech.doc;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class JsonDeserializerTest
{
	@Test
	public void testMapKeyOrder() throws Exception
	{
		JsonDeserializer deserializer = new JsonDeserializer();
		Object decoded = deserializer.decode("" + 
			"{" +
			"  customization: []," +
			"  config: []," +
			"  preprocessing: []," +
			"  runtime: []" +
			"}");
        assertTrue(decoded instanceof Map);
		@SuppressWarnings("unchecked")
		Map<String,Object> map = (Map<String,Object>) decoded;
		Iterator<Map.Entry<String,Object>> iterator = map.entrySet().iterator();
		assertTrue(iterator.hasNext());
		assertEquals("customization", iterator.next().getKey());
		assertTrue(iterator.hasNext());
		assertEquals("config", iterator.next().getKey());
		assertTrue(iterator.hasNext());
		assertEquals("preprocessing", iterator.next().getKey());
		assertTrue(iterator.hasNext());
		assertEquals("runtime", iterator.next().getKey());
		assertFalse(iterator.hasNext());
    }
}
