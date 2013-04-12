package net.ech.doc;

import java.io.IOException;

public class ChildDocumentResolver
	implements DocumentResolver
{
	private Document document;
	private String keyPrefix;

	public ChildDocumentResolver(Document document)
	{
		this(document, "");
	}

	public ChildDocumentResolver(Document document, String keyPrefix)
	{
		this.document = document;
		this.keyPrefix = keyPrefix;
	}

	@Override
	public DocumentProducer resolve(String key)
		throws IOException
	{
		String fullKey = keyPrefix + key;
		Document sub = document.find(fullKey);
		if (sub.isNull()) {
			throw new IOException(document.getSource() + ": ." + fullKey + ": child document not found");
		}
		return sub;
	}
}
