package net.ech.util;

import org.junit.*;
import static org.junit.Assert.assertEquals;

public class DocumentExceptionTest
{
	@Test
	public void testDefaultConstructor() throws Exception
	{
		throwAndAssert(new DocumentException(), null, null);
	}

	@Test
	public void testMessageConstructor() throws Exception
	{
		String message = "HI";
		throwAndAssert(new DocumentException(message), message, null);
	}

	@Test
	public void testCauseConstructor() throws Exception
	{
		Exception cause = new Exception("oops");
		throwAndAssert(new DocumentException(cause), "java.lang.Exception: oops", cause);
	}

	@Test
	public void testMessageCauseConstructor() throws Exception
	{
		Exception cause = new Exception("oops");
		throwAndAssert(new DocumentException("HI", cause), "HI", cause);
	}

	private void throwAndAssert(DocumentException de, String expectedMessage, Throwable expectedCause)
	{
		try
		{
			throw de;
		}
		catch (DocumentException thrown)
		{
			assertEquals(expectedMessage, thrown.getMessage());
			assertEquals(expectedCause, thrown.getCause());
		}
	}
}
