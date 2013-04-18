package net.ech.doc;

import java.io.IOException;

/**
 * A DocumentTransform takes a document as input and produces a document
 * as output. 
 */
public interface DocumentTransform
{
	public Document transform(Document document)
		throws IOException;
}
