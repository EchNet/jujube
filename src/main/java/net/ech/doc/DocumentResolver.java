package net.ech.doc;

import java.io.IOException;

/**
 * A DocumentResolver resolves a key to the location of a document and creates a producer for
 * the document at that location.
 */
public interface DocumentResolver
{
	public DocumentProducer resolve(String key)
		throws IOException;
}
