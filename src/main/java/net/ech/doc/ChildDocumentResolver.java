package net.ech.doc;

import java.io.IOException;

public class ChildDocumentResolver
	implements DocumentResolver
{
	private DocumentProducer producer;

	public ChildDocumentResolver()
	{
	}

	public ChildDocumentResolver(DocumentProducer producer)
	{
		this.producer = producer;
	}

	public DocumentProducer getProducer()
	{
		return producer;
	}

	public void setProducer(DocumentProducer producer)
	{
		this.producer = producer;
	}

	@Override
	public DocumentProducer resolve(String key)
		throws IOException
	{
		Document document = producer.produce();
		Document sub = document.find(key);
		if (sub.isNull()) {
			throw new IOException(document.getSource() + ": ." + key + ": child document not found");
		}
		return sub;
	}
}
