package net.ech.doc;

import java.io.IOException;

/**
 * A ProxyDocumentResolver delegates to another DocumentResolver, after optionally mutating the document key.
 * the document at that location.
 */
public class ProxyDocumentResolver
	implements DocumentResolver
{
	private DocumentResolver inner;

	public ProxyDocumentResolver(DocumentResolver inner)
	{
		this.inner = inner;
	}

	@Override
	public DocumentProducer resolve(String key)
		throws IOException
	{
		return inner.resolve(mutateDocumentKey(key));
	}

	/**
	 * Override this to add key mutation.
	 */
	protected String mutateDocumentKey(String key)
	{
		return key;
	}
}
