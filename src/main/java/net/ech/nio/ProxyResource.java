package net.ech.nio;

import net.ech.codec.*;
import java.io.*;

/**
 * 
 */
public class ProxyResource
	implements Resource
{
	private Resource inner;

	public ProxyResource(Resource inner)
	{
		this.inner = inner;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString()
	{
		return "==>" + inner.toString();
	}

	/**
	 * @inheritDoc
	 */
	@Override
    public ItemHandle resolve(Query query)
        throws IOException
	{
		return inner.resolve(query);
	}
}
