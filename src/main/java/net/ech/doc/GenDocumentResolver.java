package net.ech.doc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;

public class GenDocumentResolver
	implements DocumentResolver
{
	private Class appClass;

	// There is no way to configure this class yet, so just instantiate it one time here.
	private JsonDeserializer json = new JsonDeserializer();

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
			return new FileDocumentProducer(json, key, addJsonExt(stripPrefix(key)));
		}
		else if (key.startsWith("resource:")) {
			return new ResourceDocumentProducer(json, key, appClass.getClassLoader(), addJsonExt(stripPrefix(key)));
		}
		else if (key.startsWith("http:")) {
			return new UrlDocumentProducer(json, key);
		}
		else {
			return new FileDocumentProducer(json, key, addJsonExt(key));
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
