package net.ech.nio;

abstract public class AbstractMetadata
	implements Metadata
{
	@Override
	public String getMimeType()
	{
		return null;
	}

	@Override
	public String getCharacterEncoding()
	{
		return null;
	}

	@Override
	public Long getCachePeriod()
	{
		return null;
	}
}
