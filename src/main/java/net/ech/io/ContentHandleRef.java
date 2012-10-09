package net.ech.io;

/**
 * A drop of glue.
 */
public class ContentHandleRef
	extends AbstractContentSource
	implements ContentSource, ContentQuery
{
	private ContentHandle contentHandle;

	public ContentHandleRef(ContentHandle contentHandle)
	{
		this.contentHandle = contentHandle;
	}

	@Override
	public ContentHandle resolve(ContentRequest request)
	{
		return contentHandle;
	}

	@Override
	public ContentHandle query()
	{
		return contentHandle;
	}
}
