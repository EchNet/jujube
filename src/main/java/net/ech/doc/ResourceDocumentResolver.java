package net.ech.doc;

import java.io.IOException;

public class ResourceDocumentResolver
	implements DocumentResolver
{
	private Class<?> appClass;

	public ResourceDocumentResolver()
	{
		this(ResourceDocumentResolver.class);
	}

	public ResourceDocumentResolver(Class<?> appClass)
	{
		setAppClass(appClass);
	}

	public Class<?> getAppClass()
	{
		return appClass;
	}

	public void setAppClass(Class<?> appClass)
	{
		this.appClass = appClass;
	}

	@Override
	public DocumentProducer resolve(String key)
		throws IOException
	{
		return new StreamDocumentProducer(new ResourceDocumentSource(key, appClass.getClassLoader()));
	}
}
