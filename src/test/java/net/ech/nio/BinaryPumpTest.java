package net.ech.nio;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BinaryPumpTest
{
	@Test
	public void testInitialState() throws Exception
	{
		BinaryPump pump = new BinaryPump(new BinaryPump.Config());
		assertEquals(ProcessStatus.INITIAL, pump.getProcessStatus());
		assertEquals(0, pump.getBytesRead());
		assertEquals(0, pump.getBytesWritten());
	}

	@Test
	public void testCleanFinalState() throws Exception
	{
		final int NBYTES = 100;

		BinaryPump.Config config = new BinaryPump.Config();
		config.setInputStream(new DummyInputStream(NBYTES, null));
		config.addOutputStream(new DummyOutputStream(NBYTES));
		BinaryPump pump = new BinaryPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(NBYTES, pump.getBytesRead());
		assertEquals(NBYTES, pump.getBytesWritten());
	}

	@Test
	public void testMultiRunError() throws Exception
	{
		BinaryPump.Config config = new BinaryPump.Config();
		config.setInputStream(new DummyInputStream(10, null));
		config.addOutputStream(new DummyOutputStream(10));
		BinaryPump pump = new BinaryPump(config);
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

		BinaryPump.Config config = new BinaryPump.Config();
		config.setInputStream(new DummyInputStream(NBYTES, null));
		config.addOutputStream(new DummyOutputStream(NBYTES));
		config.addOutputStream(new DummyOutputStream(NBYTES));
		config.addOutputStream(new DummyOutputStream(NBYTES));
		BinaryPump pump = new BinaryPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(NBYTES, pump.getBytesRead());
		assertEquals(NBYTES * 3, pump.getBytesWritten());
	}

	@Test
	public void testInputErrorHandling() throws Exception
	{
		ErrorLog errorLog = new ErrorLog();

		BinaryPump.Config config = new BinaryPump.Config();
		config.setInputStream(new DummyInputStream(0, new IOException("oops")));
		config.addOutputStream(new DummyOutputStream(0));
		config.setErrorLog(errorLog);
		BinaryPump pump = new BinaryPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(0, pump.getBytesRead());
		assertEquals(0, pump.getBytesWritten());
		assertEquals(1, errorLog.entries.size());
		assertEquals("fatal", errorLog.entries.get(0).severity);
		assertEquals("oops", errorLog.entries.get(0).msg);
	}

	@Test
	public void testOutputErrorHandling() throws Exception
	{
		ErrorLog errorLog = new ErrorLog();

		BinaryPump.Config config = new BinaryPump.Config();
		config.setInputStream(new DummyInputStream(100, null));
		config.addOutputStream(new DummyOutputStream(0));
		config.setErrorLog(errorLog);
		BinaryPump pump = new BinaryPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(100, pump.getBytesRead());
		assertEquals(0, pump.getBytesWritten());
		assertEquals(1, errorLog.entries.size());
		assertEquals("fatal", errorLog.entries.get(0).severity);
		assertEquals("full", errorLog.entries.get(0).msg);
	}

	@Test
	public void testMultiOutputErrorHandling() throws Exception
	{
		ErrorLog errorLog = new ErrorLog();

		BinaryPump.Config config = new BinaryPump.Config();
		config.setInputStream(new DummyInputStream(10000, null));
		config.addOutputStream(new DummyOutputStream(0));
		config.addOutputStream(new DummyOutputStream(9000));
		config.setErrorLog(errorLog);
		BinaryPump pump = new BinaryPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(10000, pump.getBytesRead());
		assertEquals(BinaryPump.DEFAULT_BUFFER_SIZE, pump.getBytesWritten());
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

		BinaryPump.Config config = new BinaryPump.Config();
		config.setInputStream(new DummyInputStream(200, null));
		config.addOutputStream(new DummyOutputStream(0));
		config.addOutputStream(new DummyOutputStream(110));
		config.setBufferSize(100);
		config.setErrorLog(errorLog);
		BinaryPump pump = new BinaryPump(config);
		pump.run();

		assertEquals(ProcessStatus.TERMINAL, pump.getProcessStatus());
		assertEquals(200, pump.getBytesRead());
		assertEquals(100, pump.getBytesWritten());
	}

	public static class DummyInputStream
		extends ByteArrayInputStream
	{
		private int bytesRemaining;
		private IOException error;

		public DummyInputStream(int bytesRemaining, IOException error)
		{
			super(new byte[0], 0, 0);
			this.bytesRemaining = bytesRemaining;
			this.error = error;
		}

		@Override
		public int read(byte[] buf) 
			throws IOException
		{
			if (bytesRemaining > 0) {
				int bc = Math.min(bytesRemaining, buf.length);
				bytesRemaining -= bc;
				return bc;
			}
			else if (error != null) {
				throw error;
			}
			return 0;
		}
	}

	public static class DummyOutputStream
		extends FilterOutputStream
	{
		private int capacity;

		public DummyOutputStream(int capacity)
		{
			super(new ByteArrayOutputStream());
			this.capacity = capacity;
		}

		@Override
		public void write(byte[] buf, int off, int len)
			throws IOException
		{
			if (len > capacity) {
				throw new IOException("full");
			}
			capacity -= len;
		}
	}
}
