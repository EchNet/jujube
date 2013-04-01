package net.ech.doc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;

public class JsonFileDocumentLoader
	implements DocumentLoader
{
	private JsonDeserializer json = new JsonDeserializer();

	@Override
	public Document load(String key)
		throws IOException
	{
		Reader reader = new BufferedReader(new FileReader(key));
		try {
			return new Document(json.read(reader), key);
		}
		finally {
			reader.close();
		}
	}
}
