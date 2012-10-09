package net.ech.util;

import net.ech.util.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import org.springframework.mock.web.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JavascriptLexerTest
{
	String input;
	List<String> tokens;
	String output;

	@Before
	public void setUp()
	{
		input = null;
		tokens = null;
		output = null;
	}

    @Test
    public void testSimple() throws Exception
    {
		input = "a + b;";
		run();
		List<String> expected = Arrays.asList(new String[] { 
			"a", " ", "+", " ", "b", ";"
		});
		assertEquals(expected, tokens);
    }

    @Test
    public void testSimpleWhiteSpaceIgnored() throws Exception
    {
		input = "   a + b; \n\n\n";
		run(true, false);
		List<String> expected = Arrays.asList(new String[] { 
			"a", "+", "b", ";"
		});
		assertEquals(expected, tokens);
    }

    @Test
    public void testCxxComment() throws Exception
    {
		input = "a + b; // that's addition, kids\n  a - b;";
		run();
		List<String> expected = Arrays.asList(new String[] { 
			"a", " ", "+", " ", "b", ";", " ",
			"// that's addition, kids\n",
			"  ", "a", " ", "-", " ", "b", ";"
		});
		assertEquals(expected, tokens);
    }

    @Test
    public void testCxxCommentIgnored() throws Exception
    {
		input = "a + b; // that's addition, kids\n  a - b;";
		run(false, true);
		List<String> expected = Arrays.asList(new String[] { 
			"a", " ", "+", " ", "b", ";", " ",
			"  ", "a", " ", "-", " ", "b", ";"
		});
		assertEquals(expected, tokens);
    }

    @Test
    public void testCComment() throws Exception
    {
		input = "func(ay,b,c); /* It's easy as \n */ 123;";
		run();
		List<String> expected = Arrays.asList(new String[] { 
			"func", "(", "ay", ",", "b", ",", "c", ")", ";", " ",
			"/* It's easy as \n */",
			" ", "123", ";"
		});
		assertEquals(expected, tokens);
    }

    @Test
    public void testCCommentIgnored() throws Exception
    {
		input = "func(ay,b,c); /* It's easy as \n */ 123;";
		run(false, true);
		List<String> expected = Arrays.asList(new String[] { 
			"func", "(", "ay", ",", "b", ",", "c", ")", ";", " ",
			" ", "123", ";"
		});
		assertEquals(expected, tokens);
    }

    @Test
    public void testQuotedString1() throws Exception
    {
		input =
			"     " +
			"\"here is a pretty simple quoted string\"" +
			"     " +
			"\" here is a\\t\\\"NASTY\\\"\\tone \"";
		run();
		assertEquals("\" here is a\\t\\\"NASTY\\\"\\tone \"", tokens.get(3));
    }

    @Test
    public void testQuotedString2() throws Exception
    {
		input =
			"     " +
			"'here is a pretty simple quoted string'" +
			"     " +
			"' here is a\\t\\'NASTY\\'\\tone '";
		run();
		assertEquals("' here is a\\t\\'NASTY\\'\\tone '", tokens.get(3));
    }

    @Test
    public void testUnclosedQuotedString() throws Exception
    {
		input =
			"     " +
			"'here is an unclosed quoted string";
		run();
		assertEquals("'here is an unclosed quoted string", tokens.get(1));
    }

	private void run() throws Exception
	{
		run(false, false);
	}

	private void run(boolean ignoreWhitespace, boolean ignoreComments) throws Exception
	{
		JavascriptLexer lexer = new JavascriptLexer(input);
		lexer.setIgnoreWhitespace(ignoreWhitespace);
		lexer.setIgnoreComments(ignoreComments);
		tokens = new ArrayList<String>();
		while (lexer.advance() >= 0) {
			tokens.add(lexer.getText());
		}
		output = "";
		for (String token : tokens) {
			output += token;
		}
	}
}
