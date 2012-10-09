package net.ech.io;

import java.io.*;
import java.net.*;
import java.util.*;

public class PathContentSource
	extends AggregateContentSource
	implements ContentSource
{
	public void addContentSource(ContentSource source)
	{
		addChild(source);
	}

	@Override
    public ContentHandle resolve(ContentRequest request)
        throws IOException
	{
		List<ContentSource> children = getChildren();

		if (children.size() == 0) {
			throw new IOException("empty source path");
		}

		for (ContentSource source : children) {
			try {
				return source.resolve(request);
			}
			catch (FileNotFoundException e) {
			}
		}

		throw new FileNotFoundException(request.getPath() + ": not found in " + getChildren());
	}
}
