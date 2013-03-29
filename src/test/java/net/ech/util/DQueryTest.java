package net.ech.util;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DQueryTest
{
	@Test
	public void testNullValue() throws Exception
	{
		DQuery dq = new DQuery(null);
		assertEquals(null, dq.get());
	}

	@Test
	public void testNullDocumentName() throws Exception
	{
		assertNull(new DQuery(null).getPath().getDocumentName());
	}

	@Test
	public void testNonNullDocumentName() throws Exception
	{
		assertEquals("doc", new DQuery(null, "doc").getPath().getDocumentName());
	}

	@Test
	public void testNullIsNull() throws Exception
	{
		DQuery dq = new DQuery(null);
		assertTrue(dq.isNull());
	}

	@Test
	public void testNullParent() throws Exception
	{
		DQuery dq = new DQuery(null);
		assertNull(dq.getParent());
	}

	@Test
	public void testNullPath() throws Exception
	{
		DQuery dq = new DQuery(null);
		assertEquals(new DPath(), dq.getPath());
	}

	@Test
	public void testSimpleValue() throws Exception
	{
		DQuery dq = new DQuery("abc");
		assertEquals("abc", dq.get());
	}

	@Test
	public void testSimpleIsNull() throws Exception
	{
		DQuery dq = new DQuery("abc");
		assertTrue(!dq.isNull());
	}

	@Test
	public void testSimpleParent() throws Exception
	{
		DQuery dq = new DQuery("abc");
		assertNull(dq.getParent());
	}

	@Test
	public void testSimplePath() throws Exception
	{
		DQuery dq = new DQuery("abc");
		assertEquals(new DPath(), dq.getPath());
	}

	@Test
	public void testMapEntryValue() throws Exception
	{
		DQuery dq = new DQuery(new Hash("abc", 123)).find("abc");
		assertEquals(new Integer(123), dq.get());
	}

	@Test
	public void testMapEntryIsNull() throws Exception
	{
		DQuery dq = new DQuery(new Hash("abc", 123)).find("abc");
		assertTrue(!dq.isNull());
	}

	@Test
	public void testMapEntryParent() throws Exception
	{
		DQuery parent = new DQuery(new Hash("abc", 123));
		DQuery dq = parent.find("abc");
		assertEquals(parent, dq.getParent());
	}

	@Test
	public void testMapEntryPath() throws Exception
	{
		DQuery dq = new DQuery(new Hash("abc", 123)).find("abc");
		assertEquals(new DPath("abc"), dq.getPath());
	}

	@Test
	public void testUndefMapEntryValue() throws Exception
	{
		DQuery dq = new DQuery(new Hash("abc", 123)).find("def");
		assertNull(dq.get());
	}

	@Test
	public void testUndefMapEntryIsNull() throws Exception
	{
		DQuery dq = new DQuery(new Hash("abc", 123)).find("def");
		assertTrue(dq.isNull());
	}

	@Test
	public void testUndefMapEntryParent() throws Exception
	{
		DQuery parent = new DQuery(new Hash("abc", 123));
		DQuery dq = parent.find("def");
		assertEquals(parent, dq.getParent());
	}

	@Test
	public void testUndefMapEntryPath() throws Exception
	{
		DQuery dq = new DQuery(new Hash("abc", 123)).find("def");
		assertEquals(new DPath("def"), dq.getPath());
	}

	@Test
	public void testListEntryValue() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		DQuery dq = new DQuery(list).find(new DPath(1));
		assertEquals("1", dq.get());
	}

	@Test
	public void testListEntryIsNull() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		DQuery dq = new DQuery(list).find(new DPath(1));
		assertTrue(!dq.isNull());
	}

	@Test
	public void testListEntryParent() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		DQuery parent = new DQuery(list);
		DQuery dq = parent.find(new DPath(1));
		assertEquals(parent, dq.getParent());
	}

	@Test
	public void testListEntryPath() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		DQuery dq = new DQuery(list).find(new DPath(1));
		assertEquals(new DPath(1), dq.getPath());
	}

	@Test
	public void testUndefListEntryValue() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		DQuery dq = new DQuery(list).find(new DPath(5));
		assertNull(dq.get());
	}

	@Test
	public void testUndefListEntryIsNull() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		DQuery dq = new DQuery(list).find(new DPath(5));
		assertTrue(dq.isNull());
	}

	@Test
	public void testUndefListEntryParent() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		DQuery parent = new DQuery(list);
		DQuery dq = parent.find(new DPath(5));
		assertEquals(parent, dq.getParent());
	}

	@Test
	public void testUndefListEntryPath() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "0", "1", "2" });
		DQuery dq = new DQuery(list).find(new DPath(5));
		assertEquals(new DPath(5), dq.getPath());
	}

	@Test
	public void testBadIndexEntryValue() throws Exception
	{
		DQuery dq = new DQuery("hey").find(new DPath(5));
		assertNull(dq.get());
	}

	@Test
	public void testBadIndexEntryIsNull() throws Exception
	{
		DQuery dq = new DQuery("hey").find(new DPath(5));
		assertTrue(dq.isNull());
	}

	@Test
	public void testBadIndexEntryPath() throws Exception
	{
		DQuery dq = new DQuery("hey").find(new DPath(5));
		assertEquals(new DPath(5), dq.getPath());
	}

	@Test
	public void testMultiMapEntryValue() throws Exception
	{
		List<Hash> list = Arrays.asList(new Hash[] { new Hash(), new Hash("animal", "gopher"), new Hash("animal", "elk") });
		DQuery dq = new DQuery(list).find("animal");
		assertEquals(Arrays.asList(new String[] { "gopher", "elk" }), dq.get());
	}

	@Test
	public void testMultiMapEntryIsNull() throws Exception
	{
		List<Hash> list = Arrays.asList(new Hash[] { new Hash(), new Hash(), new Hash() });
		DQuery dq = new DQuery(list).find("animal");
		assertTrue(!dq.isNull());
	}

	@Test
	public void testMultiMapEntryParent() throws Exception
	{
		List<Hash> list = Arrays.asList(new Hash[] { new Hash(), new Hash("animal", "gopher"), new Hash("animal", "elk") });
		DQuery parent = new DQuery(list);
		DQuery dq = parent.find("animal");
		assertEquals(parent, dq.getParent());
	}

	@Test
	public void testMultiMapEntryPath() throws Exception
	{
		List<Hash> list = Arrays.asList(new Hash[] { new Hash(), new Hash("animal", "gopher"), new Hash("animal", "elk") });
		DQuery dq = new DQuery(list).find("animal");
		assertEquals(new DPath("animal"), dq.getPath());
	}

	@Test
	public void testSizeOfMap() throws Exception
	{
        Hash hash =  new Hash().addEntry("1", 1).addEntry("2", 2).addEntry("3", 3);
        assertEquals(3, new DQuery(hash).getSize());
	}

	@Test
	public void testSizeOfList() throws Exception
	{
		List<Object> list = new ArrayList<Object>();
		list.add("1");
		list.add("2");
        assertEquals(2, new DQuery(list).getSize());
	}

	@Test
	public void testSizeOfScalar() throws Exception
	{
		assertEquals(1, new DQuery("hi").getSize());
	}

	@Test
	public void testSizeOfNull() throws Exception
	{
		assertEquals(0, new DQuery(null).getSize());
	}

	@Test
	public void testRebasedValue() throws Exception
	{
		Hash value = new Hash("a", new Hash("b", new Hash("c", new Hash("d", new Hash()))));
		assertEquals(new Hash(), new DQuery(value).find("a.b.c.d").rebase().get());
	}

	@Test
	public void testRebasedPath() throws Exception
	{
		Hash value = new Hash("a", new Hash("b", new Hash("c", new Hash("d", new Hash()))));
		assertEquals(new DPath(), new DQuery(value).find("a.b.c.d").rebase().getPath());
	}

	@Test
	public void testRebasedParent() throws Exception
	{
		Hash value = new Hash("a", new Hash("b", new Hash("c", new Hash("d", new Hash()))));
		assertNull(new DQuery(value).find("a.b.c.d").rebase().getParent());
	}

	@Test
	public void testEachOfMap() throws Exception
	{
		final DQuery parent = new DQuery(new Hash().addEntry("foo", "bar"));
		final List<Integer> counts = new ArrayList<Integer>();
		counts.add(0);
		parent.each(new DHandler() {
			public void handle(DQuery child) throws DocumentException {
				counts.set(0, counts.get(0).intValue() + 1);
			}
		});
		assertEquals(1, counts.get(0).intValue());
	}

	@Test
	public void testEachValueOfList() throws Exception
	{
		List<Object> expected = new ArrayList<Object>();
		expected.add("abc");
		expected.add(123);

		final List<Object> recreated = new ArrayList<Object>();

		new DQuery(expected).each(new DHandler() {
			public void handle(DQuery child) throws DocumentException {
				recreated.add(child.get());
			}
		});

		assertEquals(expected, recreated);
	}

	@Test
	public void testEachPathOfList() throws Exception
	{
		List<Object> list = new ArrayList<Object>();
		list.add("abc");
		list.add(123);

		final List<DPath> expected = new ArrayList<DPath>();
		expected.add(new DPath(0));
		expected.add(new DPath(1));

		final List<DPath> collected = new ArrayList<DPath>();

		new DQuery(list).each(new DHandler() {
			public void handle(DQuery child) throws DocumentException {
				collected.add(child.getPath());
			}
		});

		assertEquals(expected, collected);
	}

	@Test
	public void testRequireIntegerPositive() throws Exception
	{
		assertEquals(new Integer(1), new DQuery(1).require(Integer.class));
	}

	@Test
	public void testRequireIntegerNegative() throws Exception
	{
		try 
		{
			new DQuery("say").require(Integer.class);
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
		assertEquals(Collections.singletonList("arf"), new DQuery(Collections.singletonList("arf")).require(List.class));
	}

	@Test
	public void testRequireListNegative() throws Exception
	{
		try 
		{
			new DQuery("say").require(List.class);
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
		assertEquals(nugget, new DQuery(root).find(new DPath().append("list").append(1)).get());
	}

	@Test
	public void testPathOfFound() throws Exception
	{
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(0);
		list.add(1);
		list.add(2);
		Hash root = new Hash().addEntry("list", list);

		DPath path = new DPath().append("list").append(1);
		assertEquals(path, new DQuery(root).find(path).getPath());
	}

	@Test
	public void testTraversal() throws Exception
	{
		DQuery root = new DQuery(hashTree(5));
		root.find("a.a.a").require(Map.class).put("MARK", 0);
		root.find("a.b.a.b").require(Map.class).put("MARK", 0);
		root.find("b.a.a.c").require(Map.class).put("MARK", 0);
		root.find("c").require(Map.class).put("MARK", 0);
		root.require(Map.class).put("MARK", 0);

		StringBuffer result = new StringBuffer();
		traverse(root, result);
		assertEquals(":a.a.a:a.b.a.b:b.a.a.c:c:", result.toString());
	}

	@Test
	public void testCopyNull() throws Exception
	{
		assertTrue(new DQuery(null).copyDoc().isNull());
	}

	@Test
	public void testCopyString() throws Exception
	{
		assertEquals("str", new DQuery("str").copyDoc().get());
	}

	@Test
	public void testCopyInteger() throws Exception
	{
		assertEquals(new Integer(86), new DQuery(86).copyDoc().get());
	}

	@Test
	public void testCopyDate() throws Exception
	{
		Object input = new Date();
		Object output = new DQuery(input).copyDoc().get();
		assertEquals(input, output);
	}

	@Test
	public void testCopyList() throws Exception
	{
		List<Object> input = new ArrayList<Object>();
		input.add(new Date());
		input.add(new Integer(26));
		Object output = new DQuery(input).copyDoc().get();
		assertEquals(input, output);
	}

	@Test
	public void testCopyHashMakesCopy() throws Exception
	{
		Hash hash = new Hash("foo", "bar");
		assertEquals(hash, new DQuery(hash).copyDoc().get());
	}

	@Test
	public void testCopyHashCreatesNew() throws Exception
	{
		Hash hash = new Hash("foo", "bar");
		assertTrue(hash != new DQuery(hash).copyDoc().get());
	}

	@Test
	public void testCopyHashWithPreFilter() throws Exception
	{
		Hash input = new Hash("foo", "bar").addEntry("baz", new Hash());
		Hash expected = new Hash("foo", "bar");
		assertEquals(expected, new DQuery(input).copyDoc(new AbstractDFilter() {
			@Override
			public boolean preallow(DQuery child)
			{
				return !"baz".equals(child.getPath().getLast());
			}
		}).get());
	}

	@Test
	public void testCopyHashWithPostFilter() throws Exception
	{
		Hash input = new Hash("foo", "bar").addEntry("baz", new Hash());
		Hash expected = new Hash("foo", "bar");
		assertEquals(expected, new DQuery(input).copyDoc(new AbstractDFilter() {
			@Override
			public boolean postallow(DQuery child) {
				return child.getSize() > 0;
			}
		}).get());
	}

	@Test
	public void testCopyListMakesCopy() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "abc", "def", "ghij" });
		assertEquals(list, new DQuery(list).copyDoc().get());
	}

	@Test
	public void testCopyListCreatesNew() throws Exception
	{
		List<String> list = Arrays.asList(new String[] { "abc", "def", "ghij" });
		assertTrue(list != new DQuery(list).copyDoc().get());
	}

	@Test
	public void testCopyListWithPreFilter() throws Exception
	{
		List<String> input = Arrays.asList(new String[] { "abc", "def", "ghij" });
		List<String> expected = Arrays.asList(new String[] { "abc", "def" });
		assertEquals(expected, new DQuery(input).copyDoc(new AbstractDFilter() {
			@Override
			public boolean preallow(DQuery child)
			{
				return child.toString().length() == 3;
			}
		}).get());
	}

	@Test
	public void testCopyListWithPostFilter() throws Exception
	{
		List<String> input = Arrays.asList(new String[] { "abc", "def", "ghij" });
		List<String> expected = Arrays.asList(new String[] { "abc", "def" });
		assertEquals(expected, new DQuery(input).copyDoc(new AbstractDFilter() {
			@Override
			public boolean postallow(DQuery child) {
				return !"ghij".equals(child.get());
			}
		}).get());
	}

	@Test
	public void testCopyArrayMakesArray() throws Exception
	{
		String[] original = new String[] { "hi" };
		Object copy = new DQuery(original).copyDoc().get();
		assertTrue(copy.getClass().isArray());
	}

	@Test
	public void testCopyArrayMakesNewArray() throws Exception
	{
		String[] original = new String[] { "hi" };
		Object copy = new DQuery(original).copyDoc().get();
		assertTrue(original != copy);
	}

	@Test
	public void testCopyArrayCopies() throws Exception
	{
		String[] original = new String[] { "hello", "whirled" };
		String[] copy = (String[]) new DQuery(original).copyDoc().get();
		assertEquals(Arrays.asList(original), Arrays.asList(copy));
	}

	@Test
	public void testCopyNonCopyable() throws Exception
	{
		try
		{
			new DQuery(new java.net.URL("http://swoop.com")).copyDoc();
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
		assertEquals("null", new DQuery(null).toString());
	}

	@Test
	public void testToStringNonNull() throws Exception
	{
		assertEquals("hi", new DQuery("hi").toString());
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

	private static class MyDFilter extends AbstractDFilter
	{
		@Override
		public boolean preallow(DQuery child)
		{
			return !"a".equals(child.getPath().getLast());
		}

		@Override
		public boolean postallow(DQuery child)
		{
			return !"c".equals(child.getPath().getLast());
		}
	}

	private void traverse(final DQuery dq, final StringBuffer buf)
		throws IOException
	{
		if ("MARK".equals(dq.getPath().getLast())) {
			buf.append(":");
			buf.append(dq.getPath().getParent().toString());
		}
		else {
			dq.each(new DHandler() {
				public void handle(DQuery child) throws IOException {
					traverse(child, buf);
				}
			});
		}
	}
}
