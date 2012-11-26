package net.ech.util;

import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DPathTest
{
	@Test
	public void testParse() throws Exception
	{
		assertEquals(new DPath().append("a").append("b").append("c"), DPath.parse("a.b.c"));
	}

	@Test
	public void testGetDocumentNameDefault() throws Exception
	{
        assertNull(new DPath().getDocumentName());
	}

	@Test
	public void testGetDocumentName() throws Exception
	{
        assertEquals("abc", new DPath().setDocumentName("abc").getDocumentName());
	}

	@Test
	public void testStringComponent() throws Exception
	{
        DPath p1 = new DPath().append("a");
        DPath p2 = new DPath("a");
        assertEquals(p1, p2);
	}

	@Test
	public void testIntegerComponent() throws Exception
	{
        DPath p1 = new DPath().append(0);
        DPath p2 = new DPath(0);
        assertEquals(p1, p2);
	}

	@Test
	public void testNotEquals() throws Exception
	{
        DPath p1 = new DPath(1);
        DPath p2 = new DPath(0);
        assertTrue(!p1.equals(p2));
	}

	@Test
	public void testCopyConstructor() throws Exception
	{
        DPath original = new DPath().append("a").append("b").append(3);
        DPath dPath = new DPath(original);
        assertEquals(original, dPath);
	}

	@Test
	public void testGetParent() throws Exception
	{
        DPath original = new DPath().append("a").append("b").append(3).getParent();
        DPath dPath = new DPath().append("a").append("b");
        assertEquals(original, dPath);
	}

	@Test
	public void testToString() throws Exception
	{
        DPath dPath = new DPath().append("a").append("b").append(3);
        assertEquals("a.b[3]", dPath.toString());
	}

	@Test
	public void testToStringWithDocumentName() throws Exception
	{
        DPath dPath = new DPath().append("a").append("b").setDocumentName("lala").append(3);
        assertEquals("{lala}.a.b[3]", dPath.toString());
	}

	@Test
	public void testNotEqualNull() throws Exception
	{
		assertTrue(!new DPath().equals(null));
	}

	@Test
	public void testEqualIfDocNamesMatch() throws Exception
	{
		assertTrue(new DPath().setDocumentName("lala").equals(new DPath().setDocumentName("lala")));
	}

	@Test
	public void testNotEqualIfDocNamesDontMatch() throws Exception
	{
		assertTrue(!new DPath().setDocumentName("trala").equals(new DPath().setDocumentName("lala")));
	}

	@Test
	public void testNotEqualIfOneLacksDocName() throws Exception
	{
		assertTrue(!new DPath().equals(new DPath().setDocumentName("lala")));
	}

	@Test
	public void testHashesEqualIfDocNamesMatch() throws Exception
	{
		assertEquals(new DPath().setDocumentName("lala").hashCode(), new DPath().setDocumentName("lala").hashCode());
	}

	@Test
	public void testHashesNotEqualIfDocNamesDontMatch() throws Exception
	{
		assertTrue(new DPath().setDocumentName("").hashCode() !=  new DPath().setDocumentName("lala").hashCode());
	}

	@Test
	public void testHashesNotEqualIfOneLacksDocName() throws Exception
	{
		assertTrue(new DPath().setDocumentName("").hashCode() != new DPath().hashCode());
	}

	@Test
	public void testGetSignature() throws Exception
	{
        DPath dPath = new DPath().append("a").append("b").append(3);
        assertEquals("a.b.*", dPath.getSignature());
	}

	@Test
	public void testGetLast() throws Exception
	{
        DPath dPath = new DPath().append("a").append("b").append(3);
        assertEquals(new Integer(3), dPath.getLast());
	}
}
