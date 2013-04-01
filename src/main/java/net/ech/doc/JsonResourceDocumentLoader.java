package net.ech.doc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;

public class JsonResourceDocumentLoader
	implements DocumentLoader
{
	private ClassLoader classLoader;
	private JsonDeserializer json = new JsonDeserializer();

	public JsonResourceDocumentLoader(ClassLoader classLoader)
	{
		this.classLoader = classLoader;
	}

	@Override
	public Document load(String key)
		throws IOException
	{
		String resourceName = key + ".json";
		Reader reader = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(resourceName)));
		try {
			return new Document(json.read(reader), "resource(" + resourceName + ")");
		}
		finally {
			reader.close();
		}
	}
}
