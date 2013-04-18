package net.ech.doc;

import java.io.IOException;

public class ExternalDocumentProducer
	extends ExternalDocumentResolver
	implements DocumentProducer
{
	private String key;

	public ExternalDocumentProducer()
	{
	}

	public ExternalDocumentProducer(String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public Document produce()
		throws IOException
	{
		return resolve(key).produce();
	}
}
