package net.ech.io;

import java.io.IOException;
import net.ech.util.Hash;

abstract public class AbstractContentDrain
	implements ContentDrain
{
	/**
	 * @inheritDoc
	 */
	@Override
	public ContentHandle accept(ContentHandle contentHandle)
		throws IOException
	{
		return new JsonContentHandle(new Hash());
	}
}
