package net.ech.codec;

import java.io.*;

public class BinaryCodec
	extends AbstractCodec
	implements Codec
{
	private String contentType;

	public BinaryCodec()
	{
	}

	public BinaryCodec(String contentType)
	{
		this.contentType = contentType;
	}

	@Override
	public String getContentType()
	{
		return contentType;
	}

	@Override
	public void encode(Object obj, OutputStream outputStream)
		throws IOException
	{
		try {
			outputStream.write((byte[]) obj);
		}
		catch (ClassCastException e) {
			throw new IOException("Cannot write object of type " + obj.getClass().getName() + " as content type " + getContentType());
		}
	}
}
