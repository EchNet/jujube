package net.ech.doc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ResourceDocumentProducer
	extends StreamDocumentProducer
	implements DocumentProducer
{
	private ClassLoader classLoader;
	private String resourcePath;

	public ResourceDocumentProducer(String resourcePath)
	{
		this(null, resourcePath);
	}

	public ResourceDocumentProducer(ClassLoader classLoader, String resourcePath)
	{
		this("resource:" + resourcePath, classLoader, resourcePath);
	}

	public ResourceDocumentProducer(String source, ClassLoader classLoader, String resourcePath)
	{
		super(source);
		this.classLoader = classLoader;
		this.resourcePath = resourcePath;
	}

	@Override
	protected Reader openReader()
		throws IOException
	{
		ClassLoader classLoader = this.classLoader == null ?  getClass().getClassLoader() : this.classLoader;
		InputStream in = classLoader.getResourceAsStream(resourcePath);
		if (in == null) {    // why the above does not throw is a mystery to me.
			throw new FileNotFoundException(resourcePath + ": resource not found");
		}
		return new BufferedReader(new InputStreamReader(in));
	}
}
