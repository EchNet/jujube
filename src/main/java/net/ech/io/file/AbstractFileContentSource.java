package net.ech.io.file;

import net.ech.codec.*;
import net.ech.io.*;
import java.io.*;
import java.net.*;

/**
 * Common implementation base for FileContentSource and UrlContentSource.
 */
abstract public class AbstractFileContentSource
	extends AbstractContentSource
	implements ContentSource
{
	private boolean mStatic;
	private String extension;
	private boolean stripExtension;
	private Codec codec;

	public AbstractFileContentSource()
	{
	}

	public AbstractFileContentSource(boolean mStatic)
	{
		this.mStatic = mStatic;
	}

	/**
	 * @param extension   if this is true, then all ContentHandles returned by this source are cacheable indefinitely
	 */
	public void setStatic(boolean mStatic)
	{
		this.mStatic = mStatic;
	}

	public boolean getStatic()
	{
		return this.mStatic;
	}

	/**
	 * @param extension   if this is non-null, then all input paths have this extension appended to them.
	 */
	public void setExtension(String extension)
	{
		this.extension = extension;
	}

	public String getExtension()
	{
		return this.extension;
	}

	/**
	 * @param strip   if this is true, then all input paths are stripped of their extensions.
	 */
	public void setStripExtension(boolean strip)
	{
		this.stripExtension = strip;
	}

	public boolean getStripExtension()
	{
		return this.stripExtension;
	}

	/**
	 * Override the Codec used to interpret all content loaded from this source.  By default,
	 * the content types of individual URLs are observed.
	 */
	public void setCodec(Codec codec)
	{
		this.codec = codec;
	}

	public Codec getCodec()
	{
		return this.codec;
	}

	// Template method.
	@Override
    final public ContentHandle resolve(ContentRequest request)
        throws IOException
	{
		String uriString = request.getPath();
		try {
			URI uri = new URI(uriString);
			AbstractContentHandle ach = resolveUri(uri, request);
			ach.setSource(uriString);
			return wrap(ach);
		}
		catch (URISyntaxException e) {
			throw new IOException(uriString, e);
		}
	}

	abstract protected AbstractContentHandle resolveUri(URI uri, ContentRequest request)
		throws IOException;

	protected String processPath(String path)
	{
		if (stripExtension) {
			int slash = path.lastIndexOf('/');
			int dot = path.lastIndexOf('.');
			if (dot > slash) {
				path = path.substring(0, dot);
			}
		}
		if (extension != null) {
			path += extension;
		}
		return path;
	}

	protected ContentHandle wrap(ContentHandle contentHandle)
	{
		if (mStatic) {
			contentHandle = new CacheAdviceContentHandle(contentHandle, ContentHandle.CacheAdvice.CACHE_INDEFINITELY);
		}
		return contentHandle;
	}
}
