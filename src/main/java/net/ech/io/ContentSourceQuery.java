package net.ech.io;

import java.io.IOException;

public class ContentSourceQuery
	implements ContentQuery
{
	private ContentSource source;
	private ContentRequest request;

	public ContentSourceQuery(ContentSource source, ContentRequest request)
	{
		this.source = source;
		this.request = request;
	}

	@Override
	public ContentHandle query()
		throws IOException
	{
		return source.resolve(request);
	}

	public String toString()
	{
		return source + ":" + request;
	}
}
