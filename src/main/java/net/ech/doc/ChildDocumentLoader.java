package net.ech.doc;

import java.io.IOException;

public class ChildDocumentLoader
	implements DocumentLoader
{
	private Document document;
	private String keyPrefix;

	public ChildDocumentLoader(Document document, String keyPrefix)
	{
		this.document = document;
		this.keyPrefix = keyPrefix;
	}

	@Override
	public Document load(String key)
		throws IOException
	{
		String fullKey = keyPrefix + key;
		Document sub = document.find(fullKey);
		if (sub.isNull()) {
			throw new IOException(document.getSource() + "." + fullKey + ": child document not found");
		}
		return sub;
	}
}
