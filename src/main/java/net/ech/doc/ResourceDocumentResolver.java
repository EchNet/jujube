package net.ech.doc;

import java.io.IOException;

public class ResourceDocumentResolver
	implements DocumentResolver
{
	private Class appClass = ResourceDocumentResolver.class;

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
		return new ResourceDocumentProducer("resource:" + key, appClass.getClassLoader(), addJsonExt(key));
	}

	private static String addJsonExt(String name)
	{
		if (!name.endsWith(".json")) {
			name += ".json";
		}
		return name;
	}
}
