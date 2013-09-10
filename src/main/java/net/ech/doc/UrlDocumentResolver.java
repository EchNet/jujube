package net.ech.doc;

import java.io.IOException;

public class UrlDocumentResolver
	implements DocumentResolver
{
	@Override
	public DocumentProducer resolve(String key)
		throws IOException
	{
		return new StreamDocumentProducer(new UrlDocumentSource(key));
	}
}
