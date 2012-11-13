package net.ech.nio;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TextPumpTest
{
	@Test
	public void testInitialState() throws Exception
	{
		TextPump pump = new TextPump(new TextPump.Config());
		assertEquals(ProcessStatus.INITIAL, pump.getProcessStatus());
		assertEquals(0, pump.getCharsRead());
		assertEquals(0, pump.getCharsWritten());
	}

	@Test
	public void testCleanFinalState() throws Exception
	{
		final int NCHARS = 100;

		TextPump.Config config = new TextPump.Config();
		config.setReader(new DummyReader(NCHARS, null));
		config.addWriter(new DummyWriter(NCHARS));
		TextPump pump = new TextPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(NCHARS, pump.getCharsRead());
		assertEquals(NCHARS, pump.getCharsWritten());
	}

	@Test
	public void testMultiRunError() throws Exception
	{
		TextPump.Config config = new TextPump.Config();
		config.setReader(new DummyReader(10, null));
		config.addWriter(new DummyWriter(10));
		TextPump pump = new TextPump(config);
		pump.run();
		try {
			pump.run();
			fail("should not be reached");
		}
		catch (IllegalStateException e)
		{
		}
	}

	@Test
	public void testCleanMultiplexFinalState() throws Exception
	{
		final int NBYTES = 100;

		TextPump.Config config = new TextPump.Config();
		config.setReader(new DummyReader(NBYTES, null));
		config.addWriter(new DummyWriter(NBYTES));
		config.addWriter(new DummyWriter(NBYTES));
		config.addWriter(new DummyWriter(NBYTES));
		TextPump pump = new TextPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(NBYTES, pump.getCharsRead());
		assertEquals(NBYTES * 3, pump.getCharsWritten());
	}

	@Test
	public void testInputErrorHandling() throws Exception
	{
		ErrorLog errorLog = new ErrorLog();

		TextPump.Config config = new TextPump.Config();
		config.setReader(new DummyReader(0, new IOException("oops")));
		config.addWriter(new DummyWriter(0));
		config.setErrorLog(errorLog);
		TextPump pump = new TextPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(0, pump.getCharsRead());
		assertEquals(0, pump.getCharsWritten());
		assertEquals(1, errorLog.entries.size());
		assertEquals("fatal", errorLog.entries.get(0).severity);
		assertEquals("oops", errorLog.entries.get(0).msg);
	}

	@Test
	public void testOutputErrorHandling() throws Exception
	{
		ErrorLog errorLog = new ErrorLog();

		TextPump.Config config = new TextPump.Config();
		config.setReader(new DummyReader(100, null));
		config.addWriter(new DummyWriter(0));
		config.setErrorLog(errorLog);
		TextPump pump = new TextPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(100, pump.getCharsRead());
		assertEquals(0, pump.getCharsWritten());
		assertEquals(1, errorLog.entries.size());
		assertEquals("fatal", errorLog.entries.get(0).severity);
		assertEquals("full", errorLog.entries.get(0).msg);
	}

	@Test
	public void testMultiOutputErrorHandling() throws Exception
	{
		ErrorLog errorLog = new ErrorLog();

		TextPump.Config config = new TextPump.Config();
		config.setReader(new DummyReader(5000, null));
		config.addWriter(new DummyWriter(0));
		config.addWriter(new DummyWriter(4500));
		config.setErrorLog(errorLog);
		TextPump pump = new TextPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(5000, pump.getCharsRead());
		assertEquals(TextPump.DEFAULT_BUFFER_SIZE, pump.getCharsWritten());
		assertEquals(3, errorLog.entries.size());
		assertEquals("error", errorLog.entries.get(0).severity);
		assertEquals("full", errorLog.entries.get(0).msg);
		assertEquals("error", errorLog.entries.get(1).severity);
		assertEquals("full", errorLog.entries.get(1).msg);
		assertEquals("fatal", errorLog.entries.get(2).severity);
		assertEquals("no outlet", errorLog.entries.get(2).msg);
	}

	@Test
	public void testSetBufferSize() throws Exception
	{
		ErrorLog errorLog = new ErrorLog();

		TextPump.Config config = new TextPump.Config();
		config.setReader(new DummyReader(200, null));
		config.addWriter(new DummyWriter(0));
		config.addWriter(new DummyWriter(110));
		config.setBufferSize(100);
		config.setErrorLog(errorLog);
		TextPump pump = new TextPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(200, pump.getCharsRead());
		assertEquals(100, pump.getCharsWritten());
	}

	public static class DummyReader
		extends StringReader
	{
		private int charsRemaining;
		private IOException error;

		public DummyReader(int charsRemaining, IOException error)
		{
			super("");
			this.charsRemaining = charsRemaining;
			this.error = error;
		}

		@Override
		public int read(char[] buf) 
			throws IOException
		{
			if (charsRemaining > 0) {
				int bc = Math.min(charsRemaining, buf.length);
				charsRemaining -= bc;
				return bc;
			}
			else if (error != null) {
				throw error;
			}
			return 0;
		}
	}

	public static class DummyWriter
		extends FilterWriter
	{
		private int capacity;

		public DummyWriter(int capacity)
		{
			super(new StringWriter());
			this.capacity = capacity;
		}

		@Override
		public void write(char[] buf, int off, int len)
			throws IOException
		{
			if (len > capacity) {
				throw new IOException("full");
			}
			capacity -= len;
		}
	}
}
