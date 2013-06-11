package net.ech.doc;

/**
 * A RelativeDocumentResolver resolves documents relative to a given base path.
 */
public class RelativeDocumentResolver
	extends ProxyDocumentResolver
	implements DocumentResolver
{
	private final static String SEPARATOR_STRING = "/";
	private final static char EXTENSION_CHAR = '.';

	private String basePath;
	private String extension;

	public RelativeDocumentResolver()
	{
		this(""); 
	}

	public RelativeDocumentResolver(String basePath)
	{
		this(basePath, null);
	}

	public RelativeDocumentResolver(String basePath, String extension)
	{
		this(new ExternalDocumentResolver(), basePath, extension);
	}

	public RelativeDocumentResolver(DocumentResolver inner, String basePath)
	{
		this(inner, basePath, null);
	}

	public RelativeDocumentResolver(DocumentResolver inner, String basePath, String extension)
	{
		super(inner == null ? new ExternalDocumentResolver() : inner);
		setBasePath(basePath);
		setExtension(extension);
	}

	public void setBasePath(String basePath)
	{
		if (basePath.length() > 0 && !basePath.endsWith(SEPARATOR_STRING)) {
			basePath += SEPARATOR_STRING;
		}
		this.basePath = basePath;
	}

	public void setExtension(String extension)
	{
		this.extension = extension;
	}

	/**
	 * Add base path to the given key.
	 */
	protected String mutateDocumentKey(String key)
	{
		StringBuilder buf = new StringBuilder();
		buf.append(basePath);
		buf.append(stripKey(key));
		if (extension != null) {
			buf.append(extension);
		}
		return buf.toString();
	}

	private String stripKey(String key)
	{
		return key.startsWith(SEPARATOR_STRING) && basePath.length() > 0 ? key.substring(1) : key;
	}
}
