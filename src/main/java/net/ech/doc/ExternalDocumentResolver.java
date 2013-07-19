package net.ech.doc;

public class ExternalDocumentResolver
	extends PluginDocumentResolver
	implements DocumentResolver
{
	private final static FileDocumentResolver fileDocumentResolver = new FileDocumentResolver();

	public ExternalDocumentResolver()
	{
		addResolver("file", true, fileDocumentResolver);
		addResolver("resource", true, new ResourceDocumentResolver());
		setDefaultProtocolResolver(new DocumentResolver() {
			@Override
			public DocumentProducer resolve(String key) {
				return new UrlDocumentProducer(key);
			}
		});
		setDefaultResolver(fileDocumentResolver);
	}

	public ExternalDocumentResolver(Class<?> appClass)
	{
		addResolver("resource", true, new ResourceDocumentResolver(appClass));
	}
}
