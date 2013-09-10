package net.ech.doc;

import java.io.IOException;

/**
 * DefaultDocumentResolver implements the DocumentResolver interface based on builtin rules for
 * composing delegates based on the source key's prefix and suffix, and returning the result as
 * a DocumentProducer.  The prefix (URI scheme) determines the type of DocumentSource to use,
 * while the suffix (file name extension) determines the DocumentDeserializer.
 */
public class DefaultDocumentResolver
	implements DocumentResolver
{
	private Class<?> appClass = DefaultDocumentResolver.class;

	public DefaultDocumentResolver()
	{
	}

	public DefaultDocumentResolver(Class<?> appClass)
	{
		this.appClass = appClass;
	}

	@Override
	public DocumentProducer resolve(String key)
		throws IOException
	{
		return getDelegate(key).resolve(key);
	}

	private DocumentResolver getDelegate(String key)
	{
		if (key.indexOf(':') < 0 || key.startsWith("file:")) {
			return new FileDocumentResolver();
		}
		if (key.startsWith("resource:")) {
			return new ResourceDocumentResolver();
		}
		return new UrlDocumentResolver();
	}
}
