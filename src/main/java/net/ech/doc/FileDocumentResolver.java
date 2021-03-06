package net.ech.doc;

import java.io.IOException;

public class FileDocumentResolver
	implements DocumentResolver
{
	@Override
	public DocumentProducer resolve(String key)
		throws IOException
	{
		return new StreamDocumentProducer(new FileDocumentSource(key));
	}
}
