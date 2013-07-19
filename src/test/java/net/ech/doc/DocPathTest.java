package net.ech.doc;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DocPathTest
{
	@Test
	public void testParse() throws Exception
	{
		assertEquals(new DocPath().append("a").append("b").append("c"), DocPath.parse("a.b.c"));
	}

	@Test
	public void testStringComponent() throws Exception
	{
        DocPath p1 = new DocPath().append("a");
        DocPath p2 = new DocPath("a");
        assertEquals(p1, p2);
	}

	@Test
	public void testIntegerComponent() throws Exception
	{
        DocPath p1 = new DocPath().append(0);
        DocPath p2 = new DocPath(0);
        assertEquals(p1, p2);
	}

	@Test
	public void testNotEquals() throws Exception
	{
        DocPath p1 = new DocPath(1);
        DocPath p2 = new DocPath(0);
        assertTrue(!p1.equals(p2));
	}

	@Test
	public void testCopyConstructor() throws Exception
	{
        DocPath original = new DocPath().append("a").append("b").append(3);
        DocPath dPath = new DocPath(original);
        assertEquals(original, dPath);
	}

	@Test
	public void testNotEqualNull() throws Exception
	{
		assertTrue(!new DocPath().equals(null));
	}

	@Test
	public void testGetSignature() throws Exception
	{
        DocPath dPath = new DocPath().append("a").append("b").append(3);
        assertEquals("a.b.*", dPath.getSignature());
	}

	@Test
	public void testGetLast() throws Exception
	{
        DocPath dPath = new DocPath().append("a").append("b").append(3);
        assertEquals(new Integer(3), dPath.getLast());
	}
}
