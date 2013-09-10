package net.ech.doc;

import java.io.IOException;

/**
 * A DocumentProducer produces an in-memory document.  This interface does not specify whether a
 * new in-memory document is created each time the produce method is called.
 */
public interface DocumentProducer
{
	/**
	 * Produce the in-memory document.
	 */
	public Document produce()
		throws IOException;
}
