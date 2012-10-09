package net.ech.io;

import java.io.IOException;

abstract public class ProxyContentSource
	extends AbstractContentSource
	implements ContentSource
{
	private ContentSource inner;

	public ProxyContentSource(ContentSource inner)
	{
		this.inner = inner;
	}

	@Override
	public ContentHandle resolve(ContentRequest request)
		throws IOException
	{
		return inner == null ? new BufferedContentHandle(request.getPath(), null, null) : inner.resolve(request);
	}

	@Override
	public Object[] list(final String path)
		throws IOException
	{
		return inner.list(path);
	}
}
