package net.ech.io;

import net.ech.codec.*;
import java.io.*;

public class ProxyContentHandle
	implements ContentHandle
{
	private ContentHandle inner;

	public ProxyContentHandle(ContentHandle inner)
	{
		this.inner = inner;
	}

	@Override
	public String getSource()
	{
		return inner.getSource();
	}

	@Override
	public CacheAdvice getCacheAdvice()
	{
		return inner.getCacheAdvice();
	}

	@Override
	public String getVersion()
		throws IOException
	{
		return inner.getVersion();
	}

	@Override
	public Codec getCodec()
		throws IOException
	{
		return inner.getCodec();
	}

	@Override
	public Object getDocument()
		throws IOException
	{
		return inner.getDocument();
	}

	@Override
	public void write(OutputStream outputStream)
		throws IOException
	{
		inner.write(outputStream);
	}

	@Override
	public void write(Writer writer)
		throws IOException
	{
		inner.write(writer);
	}

	public String getContentType()
		throws IOException
	{
		return getCodec().getContentType();
	}
}
