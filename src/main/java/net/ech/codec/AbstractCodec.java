package net.ech.codec;

import java.io.*;

abstract public class AbstractCodec
	implements Codec
{
	@Override
	abstract public String getContentType();

	@Override
	public String getCharacterEncoding()
	{
		return null;
	}

	@Override
	public Object decode(InputStream inputStream)
		throws IOException
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		transferBytes(inputStream, buffer);
		return buffer.toByteArray();
	}

	@Override
	public void encode(Object obj, OutputStream outputStream)
		throws IOException
	{
		throw new IOException(this.getClass().getName() + ": encode() not implemented");
	}

	@Override
	public void encode(Object obj, Writer writer)
		throws IOException
	{
		throw new IOException(this.getClass().getName() + ": cannot write to character output stream");
	}

	@Override
	public void write(InputStream inputStream, Writer writer)
		throws IOException
	{
		throw new IOException(this.getClass().getName() + ": cannot write to character output stream");
	}

	public static void transferBytes(InputStream inputStream, OutputStream outputStream)
		throws IOException
	{
		byte[] buf = new byte[8192];
		int bc;
		while ((bc = inputStream.read(buf)) > 0) {
			outputStream.write(buf, 0, bc);
		}
	}
}
