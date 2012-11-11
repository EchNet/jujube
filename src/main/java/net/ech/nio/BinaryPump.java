package net.ech.nio;

import java.io.*;
import java.util.*;

public class BinaryPump
	implements Runnable
{
	// Defaults.
	public static int DEFAULT_BUFFER_SIZE = 8192;

	// Configurable.
	private InputStream inputStream;
	private OutputStream[] outputStreams;
	private ErrorLog errorLog;
	private int bufferSize;

	// Process state.
	private ProcessStatusManager processStatusManager;
	private boolean[] outputErrors;
	private int bytesRead;
	private int bytesWritten;

	public static class Config
	{
		private InputStream inputStream;
		private List<OutputStream> outputStreams = new ArrayList<OutputStream>();
		private ErrorLog errorLog;
		private int bufferSize = DEFAULT_BUFFER_SIZE;

		public void setInputStream(InputStream inputStream)
		{
			this.inputStream = inputStream;
		}

		public InputStream getInputStream()
		{
			return inputStream;
		}

		public void addOutputStream(OutputStream outputStream)
		{
			outputStreams.add(outputStream);
		}

		public OutputStream[] getOutputStreams()
		{
			return outputStreams.toArray(new OutputStream[outputStreams.size()]);
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

	public BinaryPump(Config config)
	{
		this.inputStream = config.getInputStream();
		this.outputStreams = config.getOutputStreams();
		this.errorLog = config.getErrorLog();
		this.bufferSize = config.getBufferSize();

		this.processStatusManager = new ProcessStatusManager();
		this.outputErrors = new boolean[this.outputStreams.length];
		this.bytesRead = 0;
		this.bytesWritten = 0;
	}

	public ProcessStatus getProcessStatus()
	{
		return processStatusManager.getProcessStatus();
	}

	public int getBytesRead()
	{
		return bytesRead;
	}

	public int getBytesWritten()
	{
		return bytesWritten;
	}

	public void run()
	{
		processStatusManager.running();

		try {
			byte[] buf = new byte[bufferSize];
			int bc;

			while ((bc = read(buf)) > 0) {
				int oc = 0;
				bytesRead += bc;

				for (int ix = 0; ix < outputStreams.length; ++ix) {
					if (!outputErrors[ix]) {
						OutputStream outputStream = outputStreams[ix];
						if (write(outputStream, buf, bc)) {
							bytesWritten += bc;
							oc += 1;
						}
						else {
							outputErrors[ix] = true;
							if (outputStreams.length == 1) {
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

	private int read(byte[] buf)
	{
		try {
			return inputStream.read(buf);
		}
		catch (IOException e) {
			errorLog.fatal(e);
			return -1;
		}
	}

	private boolean write(OutputStream outputStream, byte[] buf, int bc)
	{
		try {
			outputStream.write(buf, 0, bc);
			return true;
		}
		catch (IOException e) {
			if (outputStreams.length == 1) {
				errorLog.fatal(e);
			}
			else {
				errorLog.error(e);
			}
			return false;
		}
	}
}
