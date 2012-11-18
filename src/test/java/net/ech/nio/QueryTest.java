package net.ech.nio;

import java.io.*;
import java.net.*;
import java.util.*;
import net.ech.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class QueryTest
{
	@Test
	public void testURISyntaxException() throws Exception
	{
		try
		{
			Query.fromUriString("? oopma / loompa");
			fail("should not be reached");
		}
		catch (URISyntaxException e)
		{
			// Expected.
		}
	}

	@Test
	public void testUriComponentsKeptAndDiscarded() throws Exception
	{
		Query query = Query.fromUriString("scheme://host:80/dir/file.ext?q=1#fragment");
		assertEquals("//host:80/dir/file.ext?q=1", query.toString());
	}

	@Test
	public void testParseAndFormatNoAuth() throws Exception
	{
		Query query = Query.fromUriString("files/file.txt");
		assertEquals("files/file.txt", query.toString());
	}

	@Test
	public void testParseAndFormatNoPath() throws Exception
	{
		Query query = Query.fromUriString("//bah");
		assertEquals("//bah/", query.toString());   // should be //bah but is not due to bug in Java URI class.
	}

	@Test
	public void testMultiParameter() throws Exception
	{
		Query query = Query.fromUriString("files/file.txt?a=b&a=c&a=b");
		assertEquals("files/file.txt?a=b&a=c&a=b", query.toString());
	}

	@Test
	public void testGetAuthority() throws Exception
	{
		Query query = Query.fromUriString("//files/file.txt");
		assertEquals("files", query.getAuthority());
	}

	@Test
	public void testSetGetAuthority() throws Exception
	{
		Query query = Query.fromUriString("//files/file.txt");
		query.setAuthority("auth");
		assertEquals("auth", query.getAuthority());
	}

	@Test
	public void testGetPath1() throws Exception
	{
		Query query = Query.fromUriString("//files/file.txt");
		assertEquals("file.txt", query.getPath());
	}

	@Test
	public void testGetPath2() throws Exception
	{
		Query query = Query.fromUriString("files/file.txt");
		assertEquals("files/file.txt", query.getPath());
	}

	@Test
	public void testSetPathToNull() throws Exception
	{
		Query query = Query.fromUriString("//bah");
		query.setPath(null);
		assertNull(query.getPath());
		assertEquals("//bah", query.toString());
	}

	@Test
	public void testSetGetQuery() throws Exception
	{
		Query query = Query.fromUriString("x");
		query.setQuery("?a=1&a=2&b=1&b=2&a=3&a=4&c&c=%7F");
		assertEquals("?a=1&a=2&b=1&b=2&a=3&a=4&c&c=%7F", query.getQuery());
	}

	@Test
	public void testRemoveParameter() throws Exception
	{
		Query query = Query.fromUriString("x?a=1&a=2&b=1&b=2&a=3&a=4&c&c=%7F");
		query.removeParameter("a");
		assertEquals("?b=1&b=2&c&c=%7F", query.getQuery());
	}

	@Test
	public void testSetParameterReplaces() throws Exception
	{
		Query query = Query.fromUriString("x?a=1&a=2&b=1&b=2&a=3&a=4&c&c=%7F");
		query.setParameter("a", "0");
		assertEquals("?a=0&b=1&b=2&c&c=%7F", query.getQuery());
	}

	@Test
	public void testSetParameterAppends() throws Exception
	{
		Query query = Query.fromUriString("x?a=1&a=2&b=1&b=2&a=3&a=4&c&c=%7F");
		query.setParameter("d", "0");
		assertEquals("?a=1&a=2&b=1&b=2&a=3&a=4&c&c=%7F&d=0", query.getQuery());
	}

	@Test
	public void testAddParameterValues() throws Exception
	{
		Query query = Query.fromUriString("x?a=1&a=2");
		query.addParameterValues("a", Arrays.asList(new String[] { "3", "4" }));
		assertEquals("?a=1&a=2&a=3&a=4", query.getQuery());
	}

	@Test
	public void testGetParameterPositive() throws Exception
	{
		Query query = Query.fromUriString("x?a=1&a=2");
		assertEquals("1", query.getParameter("a"));
	}

	@Test
	public void testGetParameterNegative() throws Exception
	{
		Query query = Query.fromUriString("x?a=1&a=2");
		assertNull(query.getParameter("b"));
	}

	@Test
	public void testGetParameterValuesPositive() throws Exception
	{
		Query query = Query.fromUriString("x?a=1&a=2");
		assertEquals(Arrays.asList(new String[] { "1", "2" }), query.getParameterValues("a"));
	}

	@Test
	public void testGetParameterValuesNegative() throws Exception
	{
		Query query = Query.fromUriString("x?a=1&a=2");
		assertEquals(new ArrayList<String>(), query.getParameterValues("b"));
	}

	@Test
	public void testPutGetAttribute() throws Exception
	{
		Query query = Query.fromUriString("files/file.txt");
		query.putAttribute("name", "value");
		assertEquals("value", query.getAttribute("name"));
	}

	@Test
	public void testGetAttributes() throws Exception
	{
		Query query = Query.fromUriString("files/file.txt");
		query.putAttribute("name", "value");
		assertEquals(new Hash("name", "value"), query.getAttributes());
	}
}
