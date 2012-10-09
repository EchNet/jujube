package net.ech.io;

import net.ech.codec.*;
import java.io.*;
import java.security.*;

public class BufferedContentHandle
	extends AbstractContentHandle
	implements ContentHandle
{
	private Codec codec;
	private String contentType;
	private Object document;

	public BufferedContentHandle(String source, Codec codec, Object document)
	{
		super(source);
		this.codec = codec;
		this.document = document;
	}

	/**
	 * Buffer a target content item in memory.
	 */
	public BufferedContentHandle(ContentHandle target)
		throws IOException
	{
		this(target.getSource(), target.getCodec(), target.getDocument());
	}

	@Override
	public String getVersion()
		throws IOException
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		write(buffer);
		return toHexString(computeHash(buffer.toByteArray()));
	}

	@Override
    public Codec getCodec()
		throws IOException
	{
		return codec;
	}

	@Override
    public Object getDocument()
		throws IOException
	{
		return document;
	}

	@Override
    public void write(OutputStream outputStream)
        throws IOException
	{
		codec.encode(getDocument(), outputStream);
	}

	@Override
    public void write(Writer writer)
        throws IOException
	{
		codec.encode(getDocument(), writer);
	}

	private static byte[] computeHash(byte[] bytes)
		throws IOException
	{
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(bytes);
			return md5.digest();
		}
		catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}
	}

	private static String toHexString(byte[] bytes)
	{
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(Integer.toString((b >>> 4) & 0xF, 16));
            buf.append(Integer.toString(b & 0xF, 16));
		}
		return buf.toString();
	}
}
