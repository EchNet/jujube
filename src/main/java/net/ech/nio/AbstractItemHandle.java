package net.ech.nio;

import java.io.*;

public abstract class AbstractItemHandle
	implements ItemHandle
{
	/**
	 * @inheritDoc
	 */
	@Override
    public InputStream openInputStream()
        throws IOException
	{
		throw new RuntimeException("openInputStream not implemented");
	}

	/**
	 * @inheritDoc
	 */
	@Override
    public Reader openReader()
        throws IOException
	{
		return new InputStreamReader(openInputStream());
	}

	/**
	 * @inheritDoc
	 */
	@Override
    public Metadata getMetadata()
	{
		throw new RuntimeException("getMetadata not implemented");
	}
}
