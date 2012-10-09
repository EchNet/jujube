package net.ech.codec;

import java.io.*;

public class TextCodec
	extends AbstractTextCodec
	implements Codec
{
	public final static String DEFAULT_CONTENT_TYPE = "text/plain";

	private String contentType = DEFAULT_CONTENT_TYPE;

	public TextCodec()
	{
	}

	public TextCodec(String contentType)
	{
		this.contentType = contentType;
	}

	public TextCodec(String contentType, String characterEncoding)
	{
		super(characterEncoding);
		this.contentType = contentType;
	}

	@Override
	public String getContentType()
	{
		return contentType;
	}

	@Override
	public Object decode(InputStream inputStream)
		throws IOException
	{
		Writer writer = new StringWriter();
		transferChars(getReader(inputStream), writer);
		return writer.toString();
	}

	@Override
	public void encode(Object obj, OutputStream outputStream)
		throws IOException
	{
		transferChars(new StringReader(obj.toString()), outputStream);
	}

	@Override
	public void encode(Object obj, Writer writer)
		throws IOException
	{
		transferChars(new StringReader(obj.toString()), writer);
	}
}
