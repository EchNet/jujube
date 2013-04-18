package net.ech.doc;

import java.io.IOException;

public class ExternalDocumentResolver
	extends ResourceDocumentResolver
	implements DocumentResolver
{
	private FileDocumentResolver fileDocumentResolver = new FileDocumentResolver();

	@Override
	public DocumentProducer resolve(String key)
		throws IOException
	{
		if (key.startsWith("file:")) {
			return fileDocumentResolver.resolve(stripPrefix(key));
		}
		else if (key.startsWith("resource:")) {
			return super.resolve(stripPrefix(key));
		}
		else if (key.startsWith("http:")) {
			return new UrlDocumentProducer(key);
		}
		else {
			return fileDocumentResolver.resolve(key);
		}
	}

	private static String stripPrefix(String key)
	{
		return key.substring(key.indexOf(':') + 1);
	}
}
