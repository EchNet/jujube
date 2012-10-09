package net.ech.service;

import net.ech.config.*;
import net.ech.io.*;
import net.ech.io.file.*;
import net.ech.util.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.junit.*;
import org.springframework.mock.web.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class TemplateExpressionParserTest
{
	@Test
	public void testParseBadFirstToken() throws Exception
	{
		try {
			parse(" ^ ");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("Cannot parse expression: {{ ^ }} - problem at/near ^", e.getMessage());
		}
	}

	@Test
	public void testParseBadTokenFollowingIdent() throws Exception
	{
		try {
			parse(" a b ");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("Cannot parse expression: {{ a b }} - problem at/near b", e.getMessage());
		}
	}

	@Test
	public void testParseBadTokenFollowingOpenParen() throws Exception
	{
		try {
			parse(" a(. ");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("Cannot parse expression: {{ a(. }} - problem at/near .", e.getMessage());
		}
	}

	@Test
	public void testParseBadTokenFollowingArg() throws Exception
	{
		try {
			parse(" a('b'? ");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("Cannot parse expression: {{ a('b'? }} - problem at/near ?", e.getMessage());
		}
	}

	@Test
	public void testParseBadTokenFollowingCloseParen() throws Exception
	{
		try {
			parse(" a('b')? ");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("Cannot parse expression: {{ a('b')? }} - problem at/near ?", e.getMessage());
		}
	}

	@Test
	public void testParseBadFinalState() throws Exception
	{
		try {
			parse("swoop('you'");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("Cannot parse expression: {{swoop('you'}} - incomplete", e.getMessage());
		}
	}

	@Test
	public void testParseSingleIdent() throws Exception
	{
		assertEquals("(this.aww)", parse(" aww ").toString());
	}

	@Test
	public void testParseFunctionCall() throws Exception
	{
		assertEquals("((this.a),)", parse("a()").toString());
	}

	@Test
	public void testParseFunctionCallWithArg() throws Exception
	{
		assertEquals("((this.a),b)", parse("a('b')").toString());
	}

	@Test
	public void testParseMemberExpr() throws Exception
	{
		assertEquals("((this.a).b)", parse("a.b").toString());
	}

	@Test
	public void testParseMedium() throws Exception
	{
		assertEquals("(((this.a),b).c)", parse("a('b') . c").toString());
	}

	@Test
	public void testParseComplex() throws Exception
	{
		assertEquals("((((this.a),b).c),d)", parse("a('b') . c('d')").toString());
	}

	@Test
	public void testAdditionExpr() throws Exception
	{
		assertEquals("((this.a) + b)", parse("a + 'b'").toString());
	}

	@Test
	public void testAdditionExpr2() throws Exception
	{
		assertEquals("((this.a) + ((this.f),(lit + (this.b))))", parse("a + f('lit' + b)").toString());
	}

	private Object parse(String expr) throws Exception
	{
		return new TemplateExpressionParser(expr).parse();
	}
}
