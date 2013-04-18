package net.ech.doc;

import java.io.IOException;

/**
 * A pass-through implementation of DocumentTransform.
 */
public class DefaultDocumentTransform
	implements DocumentTransform
{
	@Override
	public Document transform(Document document)
		throws IOException
	{
		return document;
	}
}
