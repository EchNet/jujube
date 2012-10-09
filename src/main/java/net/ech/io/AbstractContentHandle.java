package net.ech.io;

import net.ech.codec.*;
import java.io.*;
import java.util.*;

public abstract class AbstractContentHandle
	implements ContentHandle
{
	private String source;

	/**
	 * Constructor.
	 */
	public AbstractContentHandle(String source)
	{
		this.source = source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	@Override
	public String getSource()
	{
		return source;
	}

	@Override
	public CacheAdvice getCacheAdvice()
	{
		return CacheAdvice.DEFAULT;
	}

	@Override
	public String getVersion()
		throws IOException
	{
		return null;
	}

	@Override
	public Codec getCodec()
		throws IOException
	{
		return new TextCodec();
	}

	@Override
	public Object getDocument()
		throws IOException
	{
		return null;
	}

	@Override
	public void write(OutputStream outputStream)
		throws IOException
	{
	}

	@Override
	public void write(Writer writer)
		throws IOException
	{
	}

	/**
	 * Shortcut.
	 */
	public String getContentType()
		throws IOException
	{
		return getCodec().getContentType();
	}
}
