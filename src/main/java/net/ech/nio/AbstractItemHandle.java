package net.ech.nio;

import java.io.*;

public abstract class AbstractItemHandle
	implements ItemHandle
{
	/**
	 * @inheritDoc
	 */
    public InputStream presentInputStream()
        throws IOException
	{
		throw new RuntimeException("presentInputStream not implemented");
	}

	/**
	 * @inheritDoc
	 */
    public Reader presentReader()
        throws IOException
	{
		return new InputStreamReader(presentInputStream());
	}

	/**
	 * @inheritDoc
	 */
    public Object/*TODO: define Document class */ presentDocument()
        throws IOException
	{
		throw new RuntimeException("presentDocument not implemented");
	}

	/**
	 * @inheritDoc
	 */
    public boolean isLatent()
	{
		return true;
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

