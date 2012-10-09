package net.ech.service;

import net.ech.config.*;
import net.ech.codec.*;
import net.ech.io.*;
import net.ech.io.template.*;
import net.ech.util.*;
import net.ech.util.JavascriptLexer;
import java.io.IOException;
import java.net.*;

class TemplateExpressionParser
{
	public final static int N_IDENT = 0;
	public final static int N_STRING = 1;
	public final static int N_FUNCTION = 2;
	public final static int N_MEMBER = 3;
	public final static int N_PLUS = 4;
	public final static int N_THIS = 5;

	private String expr;
	private JavascriptLexer lexer;

	TemplateExpressionParser(String expr)
	{
		this.expr = expr;
		this.lexer = new JavascriptLexer(expr);
		this.lexer.setIgnoreWhitespace(true);
		this.lexer.setIgnoreComments(true);
	}

	public Node parse()
		throws IOException
	{
		// Position to first token.
		if (lexer.advance() < 0) {
			throw new IOException("empty expression");
		}
		Node node = parseExpression();
		if (!lexer.atEnd()) {
			throw parseError();
		}
		return node;
	}

	private Node parseExpression()
		throws IOException
	{
		Node node = parseOperand();
		boolean done = false;
		while (!done && !lexer.atEnd()) {
			switch (lexer.getTokenType()) {
			case '+':
				lexer.advance();
				node = new Node(N_PLUS, node, parseOperand());
				break;
			default:
				done = true;
			}
		}
		return node;
	}

	private Node parseOperand()
		throws IOException
	{
		Node node;

		if (!lexer.atEnd() && lexer.getTokenType() == JavascriptLexer.STRING_LITERAL) {
			node = new Node(N_STRING, lexer.getValue());
			lexer.advance();
		}
		else {
			node = parseTerm();
			boolean done = false;
			while (!done && !lexer.atEnd()) {
				switch (lexer.getTokenType()) {
				case '.':
					lexer.advance();
					node = new Node(N_MEMBER, node, parseTerm());
					break;
				case '(':
					lexer.advance();
					node = new Node(N_FUNCTION, node, parseFunctionArgs());
					break;
				default:
					done = true;
				}
			}
			node = toLvalue(node);
		}
		return node;
	}

	private Node parseTerm()
		throws IOException
	{
		if (!lexer.atEnd() && lexer.getTokenType() == JavascriptLexer.IDENTIFIER) {
			Node node = new Node(N_IDENT, lexer.getValue());
			lexer.advance();
			return node;
		}
		throw parseError();
	}

	private Node parseFunctionArgs()
		throws IOException
	{
		Node node = null;
		boolean done = false;
		while (!done) {
			if (!lexer.atEnd() && lexer.getTokenType() == ')') {
				lexer.advance();
				done = true;
			}
			else if (node != null) {   // Can't deal with more than one arg yet.
				throw parseError();
			}
			else {
				node = parseExpression();
			}
		}
		return node;
	}

	private static Node toLvalue(Node n)
	{
		if (n.ntype == N_IDENT) {
			n = new Node(N_MEMBER, new Node(N_THIS), n);
		}
		return n;
	}

	public static class Node
	{
		int ntype;
		String sval;
		Node lhs;
		Node rhs;

		Node(int ntype) {
			this.ntype = ntype;
		}

		Node(int ntype, String ident) {
			this.ntype = ntype;
			this.sval = ident;
		}

		Node(int ntype, Node lhs, Node rhs) {
			this.ntype = ntype;
			this.lhs = toLvalue(lhs);
			this.rhs = rhs;
		}

		public String toString()
		{
			return toString(new StringBuilder()).toString();
		}

		private StringBuilder toString(StringBuilder buf)
		{
			switch (ntype) {
			case N_THIS:
				buf.append("this");
				break;
			case N_IDENT:
			case N_STRING:
				buf.append(sval);
				break;
			default:
				buf.append("(");
				if (lhs != null) {
					lhs.toString(buf);
				}
				switch (ntype) {
				case N_FUNCTION:
					buf.append(",");
					break;
				case N_MEMBER:
					buf.append(".");
					break;
				case N_PLUS:
					buf.append(" + ");
					break;
				}
				if (rhs != null) {
					rhs.toString(buf);
				}
				buf.append(")");
			}
			return buf;
		}
	}

	private IOException parseError()
	{
		return new IOException("Cannot parse expression: {{" + expr + "}} - " + (lexer.atEnd() ? "incomplete" : ("problem at/near " + lexer.getText())));
	}
}
