package net.ech.io;

import net.ech.codec.*;
import java.io.*;

/**
 * Treat a String as a ContentHandle.
 */
public class TextContentHandle
	extends AbstractContentHandle
    implements ContentHandle
{
	private String text;
	private Codec codec;

	/**
	 * Constructor.
	 */
	public TextContentHandle(String text)
	{
		this(text, new TextCodec());
	}

	/**
	 * Constructor.
	 */
	public TextContentHandle(String text, Codec codec)
	{
		super(text);
		this.text = text;
		this.codec = codec;
	}

	public String getText()
	{
		return text;
	}

	@Override
	public Codec getCodec()
	{
		return codec;
	}

	@Override
    public Object getDocument()
        throws IOException
	{
		return codec.decode(new ByteArrayInputStream(text.getBytes(codec.getCharacterEncoding())));
	}

	/**
	 * Stream to binary output stream.
	 */
	@Override
    public void write(OutputStream outputStream)
        throws IOException
	{
		Writer writer = new OutputStreamWriter(outputStream, codec.getCharacterEncoding());
		write(writer);
		writer.flush();
	}

	/**
	 * Stream to character output stream.
	 */
	@Override
    public void write(Writer writer)
        throws IOException
	{
		writer.write(text);
	}
}
