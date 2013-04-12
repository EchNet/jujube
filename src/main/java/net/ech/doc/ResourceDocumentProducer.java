package net.ech.doc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;

public class ResourceDocumentProducer
	extends StreamDocumentProducer
	implements DocumentProducer
{
	private ClassLoader classLoader;
	private String resourcePath;

	public ResourceDocumentProducer(JsonDeserializer json, String source, ClassLoader classLoader, String resourcePath)
	{
		super(json, source);
		this.classLoader = classLoader;
		this.resourcePath = resourcePath;
	}

	@Override
	protected Reader openReader()
		throws IOException
	{
		return new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(resourcePath)));
	}
}
