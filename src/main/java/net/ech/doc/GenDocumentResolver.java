package net.ech.doc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;

public class GenDocumentResolver
	implements DocumentResolver
{
	private Class appClass;

	public Class getAppClass()
	{
		return appClass;
	}

	public void setAppClass(Class appClass)
	{
		this.appClass = appClass;
	}

	@Override
	public DocumentProducer resolve(String key)
		throws IOException
	{
		if (key.startsWith("file:")) {
			return new FileDocumentProducer(key, addJsonExt(stripPrefix(key)));
		}
		else if (key.startsWith("resource:")) {
			return new ResourceDocumentProducer(key, appClass.getClassLoader(), addJsonExt(stripPrefix(key)));
		}
		else if (key.startsWith("http:")) {
			return new UrlDocumentProducer(key);
		}
		else {
			return new FileDocumentProducer(key, addJsonExt(key));
		}
	}

	private static String stripPrefix(String key)
	{
		return key.substring(key.indexOf(':') + 1);
	}

	private static String addJsonExt(String name)
	{
		if (!name.endsWith(".json")) {
			name += ".json";
		}
		return name;
	}
}
