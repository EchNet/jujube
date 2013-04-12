package net.ech.doc;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;

abstract public class StreamDocumentProducer
	implements DocumentProducer
{
	private String source;

	public StreamDocumentProducer(String source)
	{
		this.source = source;
	}

	public String getSource()
	{
		return source;
	}

	@Override
	public Document produce()
		throws IOException
	{
		Reader reader = openReader();
		try {
			return new Document(SingletonJsonDeserializer.getInstance().read(reader), source);
		}
		finally {
			reader.close();
		}
	}

	/**
	 * Implement me.
	 */
	abstract protected Reader openReader()
		throws IOException;
}
