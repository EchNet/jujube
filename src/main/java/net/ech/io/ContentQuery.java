package net.ech.io;

import java.io.IOException;

public interface ContentQuery
{
	public ContentHandle query()
		throws IOException;
}
