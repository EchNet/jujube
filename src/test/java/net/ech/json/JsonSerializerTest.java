package net.ech.json;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JsonSerializerTest
{
	@Test
	public void testTheBasics() throws Exception
	{
        List<Object> list = new ArrayList<Object>();
        int index = 1;
        for (Object obj : new Object[] { "abc", 123, true }) {
            Map<String,Object> map = new LinkedHashMap<String,Object>();
            for (int count = 0; count < index; ++count) {
                map.put("a" + count, obj);
            }
            list.add(map);
            ++index;
        }
        expect("[{\"a0\":\"abc\"},{\"a0\":123,\"a1\":123},{\"a0\":true,\"a1\":true,\"a2\":true}]", list);
	}

	@Test
	public void testNull() throws Exception
	{
        expect("[null,null]", new Object[2]);
    }

    @Test
    public void testSpecialChars() throws Exception
    {
        List<Object> list = new ArrayList<Object>();
        list.add(" \n \b \r \" \t \f \\");
        expect("[\" \\n \\b \\r \\\" \\t \\f \\\\\"]", list);
    }

    @Test
    public void testUnicode() throws Exception
    {
        List<Object> list = new ArrayList<Object>();
        list.add("\u0001 \u001F \u007F \u009f \u2000 \u20FF");
        expect("[\"\\u0001 \\u001F \\u007F \\u009F \\u2000 \\u20FF\"]", list);
    }

    private void expect(String expected, Object obj) throws IOException
    {
		JsonSerializer serializer = new JsonSerializer();
        String jsonString = serializer.encode(obj);
        assertEquals(expected, jsonString);
    }
}
