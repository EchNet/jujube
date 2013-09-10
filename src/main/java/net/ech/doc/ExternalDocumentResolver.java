package net.ech.doc;

public class ExternalDocumentResolver
	extends DefaultDocumentResolver
	implements DocumentResolver
{
	public ExternalDocumentResolver()
	{
		super();
	}

	public ExternalDocumentResolver(Class<?> appClass)
	{
		super(appClass);
	}
}
