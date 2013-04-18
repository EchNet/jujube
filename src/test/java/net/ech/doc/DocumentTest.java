package net.ech.doc;

import java.io.*;
import java.util.*;
import net.ech.util.Hash;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DocumentTest
{
	@Test
	public void testNullValue() throws Exception
	{
		Document dq = new Document(null);
		assertEquals(null, dq.get());
	}

	@Test
	public void testNullIsNull() throws Exception
	{
		Document dq = new Document(null);
		assertTrue(dq.isNull());
	}

	@Test
	public void testNullPath() throws Exception
	{
		Document dq = new Document(null);
		assertEquals(new DocPath(), dq.getPath());
	}

	@Test
	public void testSimpleValue() throws Exception
	{
		Document dq = new Document("abc");
		assertEquals("abc", dq.get());
	}

	@Test
	public void testSimpleIsNull() throws Exception
	{
		Document dq = new Document("abc");
		assertTrue(!dq.isNull());
	}

	@Test
	public void testSimplePath() throws Exception
	{
		Document dq = new Document("abc");
		assertEquals(new DocPath(), dq.getPath());
	}

	@Test
	public void testMapEntryValue() throws Exception
	{
		Document dq = new Document(new Hash("abc", 123)).find("abc");
		assertEquals(new Integer(123), dq.get());
	}

	@Test
	public void testMapEntryIsNull() throws Exception
	{
		Document dq = new Document(new Hash("abc", 123)).find("abc");
		assertTrue(!dq.isNull());
	}

	@Test
	public void testMapEntryPath() throws Exception
	{
		Document dq = new Document(new Hash("abc", 123)).find("abc");
		assertEquals(new DocPath("abc"), dq.getPath());
	}

	@Test
	public void testUndefMapEntryValue() throws Exception
	{
		Document dq = new Document(new Hash("abc", 123)).find("def");
		assertNull(dq.get());
	}

	@Test
	public void testUndefMapEntryIsNull() throws Exception
	{
		Document dq = new Document(new Hash("abc", 123)).find("def");
		assertTrue(dq.isNull());
	}

	@Test
	public void testUndefMapEntryPath() throws Exception
	{
		Document dq = new Document(new Hash("abc", 123)).find("def");
		assertEquals(new DocPath("def"), dq.getPath());
	}

	@Test
	public void testListEntryValue() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		Document dq = new Document(list).find(new DocPath(1));
		assertEquals("1", dq.get());
	}

	@Test
	public void testListEntryIsNull() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		Document dq = new Document(list).find(new DocPath(1));
		assertTrue(!dq.isNull());
	}

	@Test
	public void testListEntryPath() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		Document dq = new Document(list).find(new DocPath(1));
		assertEquals(new DocPath(1), dq.getPath());
	}

	@Test
	public void testUndefListEntryValue() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		Document dq = new Document(list).find(new DocPath(5));
		assertNull(dq.get());
	}

	@Test
	public void testUndefListEntryIsNull() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		Document dq = new Document(list).find(new DocPath(5));
		assertTrue(dq.isNull());
	}

	@Test
	public void testUndefListEntryPath() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		Document dq = new Document(list).find(new DocPath(5));
		assertEquals(new DocPath(5), dq.getPath());
	}

	@Test
	public void testBadIndexEntryValue() throws Exception
	{
		Document dq = new Document("hey").find(new DocPath(5));
		assertNull(dq.get());
	}

	@Test
	public void testBadIndexEntryIsNull() throws Exception
	{
		Document dq = new Document("hey").find(new DocPath(5));
		assertTrue(dq.isNull());
	}

	@Test
	public void testBadIndexEntryPath() throws Exception
	{
		Document dq = new Document("hey").find(new DocPath(5));
		assertEquals(new DocPath(5), dq.getPath());
	}

	@Test
	public void testMultiMapEntryValue() throws Exception
	{
		List<Hash> list = Arrays.asList(new Hash[] { new Hash(), new Hash("animal", "gopher"), new Hash("animal", "elk") });
		Document dq = new Document(list).find("animal");
		assertEquals(Arrays.asList(new String[] { "gopher", "elk" }), dq.get());
	}

	@Test
	public void testMultiMapEntryIsNull() throws Exception
	{
		List<Hash> list = Arrays.asList(new Hash[] { new Hash(), new Hash(), new Hash() });
		Document dq = new Document(list).find("animal");
		assertTrue(!dq.isNull());
	}

	@Test
	public void testMultiMapEntryPath() throws Exception
	{
		List<Hash> list = Arrays.asList(new Hash[] { new Hash(), new Hash("animal", "gopher"), new Hash("animal", "elk") });
		Document dq = new Document(list).find("animal");
		assertEquals(new DocPath("animal"), dq.getPath());
	}

	@Test
	public void testChildrenOfMap() throws Exception
	{
		JsonDeserializer deserializer = new JsonDeserializer();
		Document doc = new Document(deserializer.decode("" + 
			"{" +
			"  customization: []," +
			"  config: []," +
			"  \"\": []," +
			"  preprocessing: []," +
			"  runtime: []" +
			"}"));
		List<Document> children = doc.children();
		assertEquals(5, children.size());
		assertEquals("customization", children.get(0).getName());
		assertEquals("config", children.get(1).getName());
		assertEquals("", children.get(2).getName());
		assertEquals("preprocessing", children.get(3).getName());
		assertEquals("runtime", children.get(4).getName());
    }

	@Test
	public void testChildrenValueOfList() throws Exception
	{
		List<Object> expected = new ArrayList<Object>();
		expected.add("abc");
		expected.add(123);
		Document parent = new Document(expected);

		List<Object> recreated = new ArrayList<Object>();

		for (Document child : parent.children()) {
			recreated.add(child.get());
		}

		assertEquals(expected, recreated);
	}

	@Test
	public void testChildrenPathOfList() throws Exception
	{
		List<Object> list = new ArrayList<Object>();
		list.add("abc");
		list.add(123);
		Document parent = new Document(list);

		final List<DocPath> expected = new ArrayList<DocPath>();
		expected.add(new DocPath(0));
		expected.add(new DocPath(1));

		List<DocPath> collected = new ArrayList<DocPath>();

		for (Document child : parent.children()) {
			collected.add(child.getPath());
		}

		assertEquals(expected, collected);
	}

	@Test
	public void testRequireIntegerPositive() throws Exception
	{
		assertEquals(new Integer(1), new Document(1).require(Integer.class));
	}

	@Test
	public void testRequireIntegerNegative() throws Exception
	{
		try 
		{
			new Document("say").require(Integer.class);
			fail("should not be reached");
		}
		catch (DocumentException e)
		{
			// Expected
		}
	}

	@Test
	public void testRequireListPositive() throws Exception
	{
		assertEquals(Collections.singletonList("arf"), new Document(Collections.singletonList("arf")).require(List.class));
	}

	@Test
	public void testRequireListNegative() throws Exception
	{
		try 
		{
			new Document("say").require(List.class);
			fail("should not be reached");
		}
		catch (DocumentException e)
		{
			// Expected
		}
	}

	@Test
	public void testFindPath() throws Exception
	{
		Hash nugget = new Hash().addEntry("color", "gold");
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(0);
		list.add(nugget);
		list.add(2);
		Hash root = new Hash().addEntry("list", list);
		assertEquals(nugget, new Document(root).find(new DocPath().append("list").append(1)).get());
	}

	@Test
	public void testPathOfFound() throws Exception
	{
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(0);
		list.add(1);
		list.add(2);
		Hash root = new Hash().addEntry("list", list);

		DocPath path = new DocPath().append("list").append(1);
		assertEquals(path, new Document(root).find(path).getPath());
	}

	@Test
	public void testCopyNull() throws Exception
	{
		assertTrue(new Document(null).copy().isNull());
	}

	@Test
	public void testCopyString() throws Exception
	{
		assertEquals("str", new Document("str").copy().get());
	}

	@Test
	public void testCopyInteger() throws Exception
	{
		assertEquals(new Integer(86), new Document(86).copy().get());
	}

	@Test
	public void testCopyDate() throws Exception
	{
		Object input = new Date();
		Object output = new Document(input).copy().get();
		assertEquals(input, output);
	}

	@Test
	public void testCopyList() throws Exception
	{
		List<Object> input = new ArrayList<Object>();
		input.add(new Date());
		input.add(new Integer(26));
		Object output = new Document(input).copy().get();
		assertEquals(input, output);
	}

	@Test
	public void testCopyHashMakesCopy() throws Exception
	{
		Hash hash = new Hash("foo", "bar");
		assertEquals(hash, new Document(hash).copy().get());
	}

	@Test
	public void testCopyHashCreatesNew() throws Exception
	{
		Hash hash = new Hash("foo", "bar");
		assertTrue(hash != new Document(hash).copy().get());
	}

	@Test
	public void testCopyListMakesCopy() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "abc", "def", "ghij" });
		assertEquals(list, new Document(list).copy().get());
	}

	@Test
	public void testCopyListCreatesNew() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "abc", "def", "ghij" });
		assertTrue(list != new Document(list).copy().get());
	}

	@Test
	public void testCopyArrayMakesArray() throws Exception
	{
		String[] original = new String[] { "hi" };
		Object copy = new Document(original).copy().get();
		assertTrue(copy.getClass().isArray());
	}

	@Test
	public void testCopyArrayMakesNewArray() throws Exception
	{
		String[] original = new String[] { "hi" };
		Object copy = new Document(original).copy().get();
		assertTrue(original != copy);
	}

	@Test
	public void testCopyArrayCopies() throws Exception
	{
		String[] original = new String[] { "hello", "whirled" };
		String[] copy = (String[]) new Document(original).copy().get();
		assertEquals(Arrays.asList(original), Arrays.asList(copy));
	}

	@Test
	public void testCopyNonCopyable() throws Exception
	{
		try
		{
			new Document(new java.net.URL("http://swoop.com")).copy();
			fail("should not be reached");
		}
		catch (IllegalArgumentException e)
		{
			// Expected.
			assertEquals("http://swoop.com: class java.net.URL not copyable", e.getMessage());
		}
	}

	@Test
	public void testToStringNull() throws Exception
	{
		assertEquals("null", new Document(null).toString());
	}

	@Test
	public void testToStringNonNull() throws Exception
	{
		assertEquals("hi", new Document("hi").toString());
	}

	@Test
	public void testExtend() throws Exception
	{
	}

	private Hash hashTree(int depth)
	{
		Hash hash = new Hash();
		if (depth > 0)
		{
			hash.addEntry("a", hashTree(depth - 1));
			hash.addEntry("b", hashTree(depth - 1));
			hash.addEntry("c", hashTree(depth - 1));
		}
		return hash;
	}
}
