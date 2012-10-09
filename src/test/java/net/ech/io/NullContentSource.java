package net.ech.io;

import net.ech.codec.TextCodec;

class NullContentSource extends AbstractContentSource
{
	@Override 
	public ContentHandle resolve(ContentRequest request)
	{
		return new BufferedContentHandle(request.getPath(), new TextCodec(), null);
	}
}
