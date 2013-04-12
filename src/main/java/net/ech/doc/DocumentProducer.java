package net.ech.doc;

import java.io.IOException;

//
// A DocumentProducer produces an in-memory document.
//
public interface DocumentProducer
{
	public Document produce()
		throws IOException;
}
