package net.ech.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractContentSource
	implements ContentSource
{
	@Override
    public ContentHandle resolve(ContentRequest request)
        throws IOException
	{
		throw new IOException(this.getClass().getName() + ": resolve() not implemented");
	}

	@Override
	public Object[] list(String path)
		throws IOException
	{
		throw new IOException(this.getClass().getName() + ": list() not implemented");
	}
}
