package net.ech.doc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

public class ResourceDocumentSource
	extends FileDocumentSource
	implements DocumentSource
{
	private ClassLoader classLoader;

	public ResourceDocumentSource(String key, ClassLoader classLoader)
		throws DocumentException
	{
		super(key);
		this.classLoader = classLoader;
	}

	@Override
	public Reader open()
		throws IOException
	{
		InputStream in = classLoader.getResourceAsStream(getPath());
		if (in == null) {    // why the above does not throw is a mystery to me.
			throw new FileNotFoundException(this + ": resource not found");
		}
		return new BufferedReader(new InputStreamReader(in));
	}
}
