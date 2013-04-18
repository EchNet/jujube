package net.ech.doc;

import java.io.IOException;

/**
 * A DocumentCacheNode produces a document and caches it for reuse.
 */
public class DocumentCacheNode
	implements DocumentProducer
{
	private DocumentProducer producer;
	private Document cached;

	public DocumentCacheNode(DocumentProducer producer)
	{
		this.producer = producer;
	}

	public synchronized void clear()
	{
		this.cached = null;
	}

	@Override
	public synchronized Document produce()
		throws IOException
	{
		if (cached == null) {
			cached = producer.produce();
		}
		return cached;
	}
}
