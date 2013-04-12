package net.ech.doc;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;

abstract public class StreamDocumentProducer
	implements DocumentProducer
{
	private JsonDeserializer json;
	private String source;

	public StreamDocumentProducer(JsonDeserializer json, String source)
	{
		this.json = json;
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
			return new Document(json.read(reader), source);
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
