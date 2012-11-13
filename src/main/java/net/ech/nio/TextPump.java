package net.ech.nio;

import java.io.*;
import java.util.*;

public class TextPump
	implements Runnable
{
	// Defaults.
	public static int DEFAULT_BUFFER_SIZE = 4096;

	// Configurable.
	private Reader reader;
	private Writer[] writers;
	private ErrorLog errorLog;
	private int bufferSize;

	// Process state.
	private ProcessStatusManager processStatusManager;
	private boolean[] outputErrors;
	private int charsRead;
	private int charsWritten;

	public static class Config
	{
		private Reader reader;
		private List<Writer> writers = new ArrayList<Writer>();
		private ErrorLog errorLog;
		private int bufferSize = DEFAULT_BUFFER_SIZE;

		public void setReader(Reader reader)
		{
			this.reader = reader;
		}

		public Reader getReader()
		{
			return reader;
		}

		public void addWriter(Writer writer)
		{
			writers.add(writer);
		}

		public Writer[] getWriter()
		{
			return writers.toArray(new Writer[writers.size()]);
		}

		public void setErrorLog(ErrorLog errorLog)
		{
			this.errorLog = errorLog;
		}

		public ErrorLog getErrorLog()
		{
			return errorLog;
		}

		public void setBufferSize(int bufferSize)
		{
			this.bufferSize = bufferSize;
		}

		public int getBufferSize()
		{
			return bufferSize;
		}
	}

	public TextPump(Config config)
	{
		this.reader = config.getReader();
		this.writers = config.getWriter();
		this.errorLog = config.getErrorLog();
		this.bufferSize = config.getBufferSize();

		this.processStatusManager = new ProcessStatusManager();
		this.outputErrors = new boolean[this.writers.length];
		this.charsRead = 0;
		this.charsWritten = 0;
	}

	public ProcessStatus getProcessStatus()
	{
		return processStatusManager.getProcessStatus();
	}

	public int getCharsRead()
	{
		return charsRead;
	}

	public int getCharsWritten()
	{
		return charsWritten;
	}

	public void run()
	{
		processStatusManager.running();

		try {
			char[] buf = new char[bufferSize];
			int bc;

			while ((bc = read(buf)) > 0) {
				int oc = 0;
				charsRead += bc;

				for (int ix = 0; ix < writers.length; ++ix) {
					if (!outputErrors[ix]) {
						Writer writer = writers[ix];
						if (write(writer, buf, bc)) {
							charsWritten += bc;
							oc += 1;
						}
						else {
							outputErrors[ix] = true;
							if (writers.length == 1) {
								return;
							}
						}
					}
				}

				if (oc == 0) {
					errorLog.fatal("no outlet");
					return;
				}
			}
		}
		finally {
			processStatusManager.terminal();
		}
	}

	private int read(char[] buf)
	{
		try {
			return reader.read(buf);
		}
		catch (IOException e) {
			errorLog.fatal(e);
			return -1;
		}
	}

	private boolean write(Writer writer, char[] buf, int bc)
	{
		try {
			writer.write(buf, 0, bc);
			return true;
		}
		catch (IOException e) {
			if (writers.length == 1) {
				errorLog.fatal(e);
			}
			else {
				errorLog.error(e);
			}
			return false;
		}
	}
}
