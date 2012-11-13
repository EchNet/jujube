package net.ech.nio.csv;

import net.ech.util.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CsvCodecTest
{
	@Test
	public void testResultIsList() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997,Ford,E350");
		assertTrue(stuff instanceof List);
	}

	@Test
	public void testResultIsListAltConstr() throws Exception
	{
		CsvCodec decoder = new CsvCodec(false);
		Object stuff = decode(decoder, "1997,Ford,E350");
		assertTrue(stuff instanceof List);
	}

	@Test
	public void testFirstLineContainsLabelsNotYetImplemented() throws Exception
	{
		try {
			new CsvCodec(true);
			fail("should not be reached");
		}
		catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testOneLineOneElement() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997,Ford,E350");
		assertEquals(1, ((List<Object>)stuff).size());
	}

	@Test
	public void testTwoLineTwoElements() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997,Ford,E350\n1998,Ford,Edsel");
		assertEquals(2, ((List<Object>)stuff).size());
	}

	@Test
	public void testTwoLineTwoElementLf() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997,Ford,E350\r1998,Ford,Edsel");
		assertEquals(2, ((List<Object>)stuff).size());
	}

	@Test
	public void testTwoLineTwoElementCrLf() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997,Ford,E350\r\n1998,Ford,Edsel");
		assertEquals(2, ((List<Object>)stuff).size());
	}

	@Test
	public void testTerminalCarriageReturn() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997,Ford,E350\n1998,Ford,Edsel\n");
		assertEquals(2, ((List<Object>)stuff).size());
	}

	@Test
	public void testValue1() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997,Ford,E350\n1998,Ford,Edsel\n");
		assertEquals("Ford", new DQuery(stuff).find(new DPath(0).append("1")).get());
	}

	@Test
	public void testValue2() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997,Ford,E350\n1998,Ford,Edsel\n");
		assertEquals("Ford", new DQuery(stuff).find(new DPath(0).append("1")).get());
	}

	@Test
	public void testValueWithSpaces1() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997 , Ford ,E350");
		assertEquals("1997 ", new DQuery(stuff).find(new DPath(0).append("0")).get());
	}

	@Test
	public void testValueWithSpaces2() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997 , Ford ,E350");
		assertEquals(" Ford ", new DQuery(stuff).find(new DPath(0).append("1")).get());
	}

	@Test
	public void testValueWithEmbeddedComma() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997,Ford,\"Super, luxury truck\"");
		assertEquals("Super, luxury truck", new DQuery(stuff).find(new DPath(0).append("2")).get());
	}

	@Test
	public void testValueWithEmbeddedQuote() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997,Ford,\"Super \"\"luxury\"\" truck\"");
		assertEquals("Super \"luxury\" truck", new DQuery(stuff).find(new DPath(0).append("2")).get());
	}

	@Test
	public void testValueWithEmbeddedLineBreak() throws Exception
	{
		CsvCodec decoder = new CsvCodec();
		Object stuff = decode(decoder, "1997,Ford,E350,\"Go get one now\nthey are going fast\"");
		assertEquals("Go get one now\nthey are going fast", new DQuery(stuff).find(new DPath(0).append("3")).get());
	}

	@Test
	public void testLabelAssignment1() throws Exception
	{
		CsvCodec decoder = new CsvCodec(new String[] { "date", "make" });
		Object stuff = decode(decoder, "1997,Ford,E350\n1998,Ford,Edsel\n");
		assertEquals("1997", new DQuery(stuff).find(new DPath(0).append("date")).get());
	}

	@Test
	public void testLabelAssignment2() throws Exception
	{
		CsvCodec decoder = new CsvCodec(new String[] { "date", "make" });
		Object stuff = decode(decoder, "1997,Ford,E350\n1998,Ford,Edsel\n");
		assertEquals("Ford", new DQuery(stuff).find(new DPath(0).append("make")).get());
	}

	@Test
	public void testLabelAssignment3() throws Exception
	{
		CsvCodec decoder = new CsvCodec(new String[] { "date", "make" });
		Object stuff = decode(decoder, "1997,Ford,E350\n1998,Ford,Edsel\n");
		assertEquals("Edsel", new DQuery(stuff).find(new DPath(1).append("2")).get());
	}

	@Test
	public void testLabelAssignmentUsingList() throws Exception
	{
		CsvCodec decoder = new CsvCodec(Collections.singletonList("date"));
		Object stuff = decode(decoder, "1997,Ford,E350");
		assertEquals("1997", new DQuery(stuff).find(new DPath(0).append("date")).get());
	}

	@Test
	public void testOpeningCommaAndClosingComma() throws Exception
	{
		CsvCodec decoder = new CsvCodec(Collections.singletonList("date"));
		Object stuff = decode(decoder, ",,");
		assertEquals(3, new DQuery(stuff).find(new DPath(0)).getSize());
	}

	@Test
	public void testUnquotedQuote() throws Exception
	{
		try {
			CsvCodec decoder = new CsvCodec(Collections.singletonList("date"));
			decode(decoder, "1998,Ford,Super \"luxury\" truck");
			fail("should not be reached");
		}
		catch (DocumentException e) {
			assertEquals("double quote not permitted at line 1(16)", e.getMessage());
		}
	}

	@Test
	public void testUnquotedQuoteOnNextLine() throws Exception
	{
		try {
			CsvCodec decoder = new CsvCodec(Collections.singletonList("date"));
			decode(decoder, "1997,Ford\n1998,Ford,Super \"luxury\" truck");
			fail("should not be reached");
		}
		catch (DocumentException e) {
			assertEquals("double quote not permitted at line 2(16)", e.getMessage());
		}
	}

	@Test
	public void testLineNumberIncrementedEvenIfLineBreakQuoted() throws Exception
	{
		try {
			CsvCodec decoder = new CsvCodec(Collections.singletonList("date"));
			decode(decoder, "1997,Ford,\"Fun?\r\nWow!\"\r\n1998,Ford,Super \"luxury\" truck");
			fail("should not be reached");
		}
		catch (DocumentException e) {
			assertEquals("double quote not permitted at line 3(16)", e.getMessage());
		}
	}

	@Test
	public void testUnexpectedCharacterFollowingQuote() throws Exception
	{
		try {
			CsvCodec decoder = new CsvCodec(Collections.singletonList("date"));
			decode(decoder, "1997,\"Ford\" ");
			fail("should not be reached");
		}
		catch (DocumentException e) {
			assertEquals("unexpected character at line 1(11)", e.getMessage());
		}
	}

	@Test
	public void testUnexpectedEndOfFile() throws Exception
	{
		try {
			CsvCodec decoder = new CsvCodec(Collections.singletonList("date"));
			decode(decoder, "1997,\"Ford");
			fail("should not be reached");
		}
		catch (DocumentException e) {
			assertEquals("unexpected end of file at line 1(10)", e.getMessage());
		}
	}

	private static Object decode(CsvCodec decoder, String text)
		throws IOException
	{
		return decoder.decode(new StringReader(text));
	}
}
