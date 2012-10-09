package net.ech.codec;

import java.io.*;

abstract public class AbstractTextCodec
	extends AbstractCodec
	implements Codec
{
	public final static String DEFAULT_CHARACTER_ENCODING = "UTF-8";

	private String characterEncoding = DEFAULT_CHARACTER_ENCODING;
	private int bufferCharSize = 4096;

	public AbstractTextCodec()
	{
	}

	public AbstractTextCodec(String characterEncoding)
	{
		this.characterEncoding = characterEncoding;
	}

	@Override
	public String getCharacterEncoding()
	{
		return characterEncoding;
	}

	@Override
	public void write(InputStream inputStream, Writer writer)
		throws IOException
	{
		transferChars(getReader(inputStream), writer);
	}

	public int transferChars(Reader reader, OutputStream outputStream)
		throws IOException
	{
		Writer writer = new OutputStreamWriter(outputStream, characterEncoding);
		int charactersTransferred = transferChars(reader, writer);
		writer.flush();
		return charactersTransferred;
	}

	public int transferChars(Reader reader, Writer writer)
		throws IOException
	{
		int charactersTransferred = 0;
		char[] buf = new char[bufferCharSize];
		int cc;
		while ((cc = reader.read(buf)) > 0) {
			writer.write(buf, 0, cc);
			charactersTransferred += cc;
		}
		return charactersTransferred;
	}

	public Reader getReader(InputStream inputStream)
		throws IOException
	{
		return new InputStreamReader(inputStream, characterEncoding);
	}
}
