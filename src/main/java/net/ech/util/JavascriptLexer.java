package net.ech.util;

import java.util.regex.*;

/**
 * A ridiculously crude lexical analyzer for just enough of the Javascript language to support Swoop templates.
 */
public class JavascriptLexer
{
	public final static int END = -1;
	public final static int STRING_LITERAL = '"';
	public final static int IDENTIFIER = 'a';
	public final static int WHITESPACE = ' ';
	public final static int COMMENT = '/';

	private String remaining;
	private boolean ignoreWhitespace;
	private boolean ignoreComments;

	private static Pattern[] pIDENT = new Pattern[] {
		Pattern.compile("[a-zA-Z0-9_\\$]+\\b")         // identifier or number
	};

	private static Pattern[] pCOMMENT = new Pattern[] {
		Pattern.compile("//.*[\\n\\r]*"),               // C++-style comment
		Pattern.compile("/\\*.*\\*/", Pattern.DOTALL)   // C-style comment
	};

	private static Pattern[] pWHITESPACE = new Pattern[] {
		Pattern.compile("(\\s)+")                       // white space
	};

	private int ttype;
	private String text;

	public JavascriptLexer(String text)
	{
		this.remaining = text;
	}

	public void setIgnoreWhitespace(boolean ignoreWhitespace)
	{
		this.ignoreWhitespace = ignoreWhitespace;
	}

	public void setIgnoreComments(boolean ignoreComments)
	{
		this.ignoreComments = ignoreComments;
	}

	public boolean atEnd()
	{
		return ttype == END;
	}

	public int advance()
	{
		for (boolean done = false; !done; ) {
			int len = 0;
			if (remaining.isEmpty()) {
				ttype = END;
				done = true;
			}
			else if ((len = grabQuotedString('\'')) > 0 ||
				(len = grabQuotedString('"')) > 0)
			{
				ttype = STRING_LITERAL;
				done = true;
			}
			else if ((len = grab(pIDENT)) > 0) {
				ttype = IDENTIFIER;
				done = true;
			}
			else if ((len = grab(pWHITESPACE)) > 0) {
				ttype = WHITESPACE;
				done = !ignoreWhitespace;
			}
			else if ((len = grab(pCOMMENT)) > 0) {
				ttype = WHITESPACE;
				done = !ignoreComments;
			}
			else {
				len = 1;
				ttype = remaining.charAt(0);
				done = true;
			}
			text = remaining.substring(0, len);
			remaining = remaining.substring(len);
		}
		return ttype;
	}

	public int getTokenType()
	{
		return ttype;
	}

	public String getText()
	{
		return text;
	}

	public String getValue()
	{
		if (ttype == STRING_LITERAL) {
			return evalQuotedString(text);
		}
		return text;
	}

	private int grabQuotedString(char quote)
	{
		if (remaining.charAt(0) == quote) {
			boolean esc = false;
			for (int i = 1; i < remaining.length(); ++i) {
				if (esc) {
					esc = false;
				}
				else {
					char c = remaining.charAt(i);
					if (c == '\\') {
						esc = true;
					}
					if (c == quote) {
						return i + 1;
					}
				}
			}
			return remaining.length();
		}
		return 0;
	}

	private static String evalQuotedString(String text)
	{
		StringBuilder buf = new StringBuilder();
		char quote = text.charAt(0);
		boolean esc = false;
		for (int i = 1; i < text.length() - 1; ++i) {
			char c = text.charAt(i);
			if (esc) {
				switch (c) {
				case 'n':
					c = '\n';
					break;
				case 't':
					c = '\t';
					break;
				case 'r':
					c = '\r';
					break;
				case 'b':
					c = '\b';
					break;
				}
				esc = false;
			}
			else if (c == '\\') {
				esc = true;
				continue;
			}
			buf.append(c);
		}
		return buf.toString();
	}

	private int grab(Pattern[] patterns)
	{
		for (Pattern p : patterns) {
			Matcher m = p.matcher(remaining);
			if (m.lookingAt()) {
				return m.end();
			}
		}
		return 0;
	}
}
