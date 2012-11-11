package net.ech.nio;

import java.util.*;

public class ErrorLog
{
	public static class Entry
	{
		public String severity;
		public long time;
		public String msg;
		public Exception exception;

		Entry(String severity, long time, String msg, Exception e)
		{
			this.severity = severity;
			this.time = time;
			this.msg = msg;
			this.exception = e;
		}
	}

	public List<Entry> entries = new ArrayList<Entry>();

	public void error(Exception e) {
		log("error", e.getMessage(), e);
	}
	public void error(String msg) {
		log("error", msg, null);
	}

	public void fatal(Exception e) {
		log("fatal", e.getMessage(), e);
	}
	public void fatal(String msg) {
		log("fatal", msg, null);
	}

	protected void log(String severity, String msg, Exception e)
	{
		entries.add(new Entry(severity, System.currentTimeMillis(), msg, e));
	}
}
