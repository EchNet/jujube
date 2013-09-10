package net.ech.doc;

import java.io.Reader;
import java.io.IOException;
import static net.ech.doc.DefaultDeserializerLogic.createDeserializer;

public class StreamDocumentProducer
	implements DocumentProducer
{
	private DocumentSource source;

	public StreamDocumentProducer(DocumentSource source)
	{
		this.source = source;
	}

	public DocumentSource getSource()
	{
		return source;
	}

	@Override
	public Document produce()
		throws IOException
	{
		Reader reader = source.open();
		try {
			Deserializer deserializer = createDeserializer(source);
			try {
				return new Document(deserializer.deserialize(reader), source.toString());
			}
			catch (IOException e) {
				throw new IOException(source + ": deserialization error", e);
			}
		}
		finally {
			reader.close();
		}
	}
}
