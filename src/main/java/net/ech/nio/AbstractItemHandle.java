package net.ech.nio;

import java.io.*;

public abstract class AbstractItemHandle
	implements ItemHandle
{
	/**
	 * @inheritDoc
	 */
    public InputStream openInputStream()
        throws IOException
	{
		throw new RuntimeException("openInputStream not implemented");
	}

	/**
	 * @inheritDoc
	 */
    public Reader openReader()
        throws IOException
	{
		return new InputStreamReader(openInputStream());
	}

	/**
	 * @inheritDoc
	 */
    public Metadata getMetadata()
        throws IOException
	{
		throw new RuntimeException("getMetadata not implemented");
	}
}
