package net.ech.io;

import java.io.IOException;

public interface ContentDrain
{
	/**
	 * Dispose of the content referenced by the given ContentHandle.
	 * @return a new ContentHandle that describes the action taken.
	 */
	public ContentHandle accept(ContentHandle contentHandle)
		throws IOException;
}
