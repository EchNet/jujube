package net.ech.nio;

import java.io.*;

public class ProxyItemHandle
	implements ItemHandle
{
	private ItemHandle inner;

	public ProxyItemHandle(ItemHandle inner)
	{
		this.inner = inner;
	}

	/**
	 * @inheritDoc
	 */
    public InputStream openInputStream()
        throws IOException
	{
		return inner.openInputStream();
	}

	/**
	 * @inheritDoc
	 */
    public Reader openReader()
        throws IOException
	{
		return inner.openReader();
	}

	/**
	 * @inheritDoc
	 */
    public Metadata getMetadata()
	{
		return inner.getMetadata();
	}

	/**
	 * @inheritDoc
	 */
	public String toString()
	{
		return "==>" + inner.toString();
	}
}
