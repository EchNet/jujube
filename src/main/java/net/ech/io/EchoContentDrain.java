package net.ech.io;

public class EchoContentDrain
	implements ContentDrain
{
	/**
	 * @inheritDoc
	 */
	public ContentHandle accept(ContentHandle contentHandle)
	{
		return contentHandle;
	}
}
