package net.ech.codec;

import net.ech.util.DocumentException;
import java.io.*;
import java.util.*;

public class CsvCodec
	extends AbstractTextCodec
	implements Codec
{
	private String[] labels;

	public CsvCodec()
	{
	}

	public CsvCodec(String characterEncoding)
	{
		super(characterEncoding);
	}

	public CsvCodec(boolean firstLineContainsLabels)
	{
		if (firstLineContainsLabels) {
			throw new IllegalArgumentException("firstLineContainsLabels not yet implemented");
		}
	}

	public CsvCodec(String[] labels)
	{
		this.labels = (String[]) labels.clone();
	}

	public CsvCodec(List<String> labels)
	{
		this.labels = (String[]) labels.toArray(new String[labels.size()]);
	}

	@Override
	public String getContentType()
	{
		return "text/csv";
	}

	@Override
	public Object decode(InputStream input)
		throws IOException
	{
		return new Decoder(new InputStreamReader(input, getCharacterEncoding())).decode();
	}

	private enum State
	{
		OPEN,
		IN_UNQUOTED,
		IN_QUOTED,
		QUOTE_JURY_OUT
	}

	private class Decoder
	{
		private Reader reader;
		private List<Map<String,String>> results = new ArrayList<Map<String,String>>();
		private Map<String,String> row = null;
        private StringBuilder buf = new StringBuilder();
        private State state = State.OPEN;
		private int line = 1;
		private int cc = 0;

		public Decoder(Reader reader)
		{
			this.reader = reader;
		}

		public List<Map<String,String>> decode()
			throws IOException
		{
			int c;
			for (int lastc = 0; (c = reader.read()) >= 0; lastc = c)
			{
				switch (c) {
				case ',':
					switch (state) {
					case IN_QUOTED:
						append(c);
						break;
					default:
						closeBuf();
					}
					++cc;
					break;
				case '"':
					switch (state) {
					case OPEN:
						state = State.IN_QUOTED;
						break;
					case IN_UNQUOTED:
						throwError("double quote not permitted");
					case IN_QUOTED:
						state = State.QUOTE_JURY_OUT;
						break;
					default:
						append('"');
						state = State.IN_QUOTED;
					}
					++cc;
					break;
				case '\r':
				case '\n':
					switch (state) {
					case IN_QUOTED:
						append(c);
						break;
					default:
						closeRow();
					}
					if (c == '\r' || lastc != '\r') {
						++line;
						cc = 0;
					}
					break;
				default:
					switch (state) {
					case OPEN:
					case IN_UNQUOTED:
						append(c);
						state = State.IN_UNQUOTED;
						break;
					case IN_QUOTED:
						append(c);
						break;
					default:
						throwError("unexpected character");
					}
					++cc;
				}
			}

			switch (state) {
			case IN_QUOTED:
				throwError("unexpected end of file");
			default:
				closeRow();
			}

			return results;
		}

		private void append(int c)
		{
			buf.append((char)c);
		}

		private void closeBuf()
		{
			if (row == null) { 
				row = new HashMap<String,String>();
				results.add(row);
			}
			row.put(getLabel(), buf.toString());
			state = State.OPEN;
			buf.setLength(0);
		}

		private String getLabel()
		{
			int ix = row.size();
			return (labels == null || ix >= labels.length)
				? Integer.toString(ix) : labels[ix];
		}

		private void closeRow()
		{
			if (row != null) {
				closeBuf();
				row = null;
			}
		}

		private String position()
		{
			return "line " + line + "(" + cc + ")";
		}

		private void throwError(String msg)
			throws DocumentException
		{
			throw new DocumentException(msg + " at " + position());
		}
	}
}
