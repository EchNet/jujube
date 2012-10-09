package net.ech.io;

import net.ech.codec.*;
import java.io.*;

/**
 * A resolved content item.
 */
public interface ContentHandle
{
	enum CacheAdvice {
		DEFAULT,
		DONT_CACHE,
		CACHE_INDEFINITELY
	}

	/**
	 * A human-readable description of the content item's source, e.g. a URI.  May be null.
	 */
	public String getSource();

	/**
	 * Find out whether this content may be cached, and if so, for how long.
	 */
	public CacheAdvice getCacheAdvice();

	/**
	 * A string that positively identifies the version of the content item referenced by
	 * this handle.  If this is not null, then the value is guaranteed by the parent 
	 * ContentSource to differ if the content item's source is modified.  This is used
	 * in conjuction with ContentRequest to suppress the reloading of cached content in
	 * the case that the content item's source has not been modified.
	 */
	public String getVersion()
		throws IOException;

	/**
	 * Responsible for MIME content type and encoding/decoding behaviors.
	 */
	public Codec getCodec()
		throws IOException;

	/**
	 * Load the content into memory.  If the content source is text, the result is a String.
	 * If the content source is JSON, the result is a DQuery-addressable object structure. 
	 * If the content source is binary, the result is a byte array.
	 */
    public Object getDocument()
        throws IOException;

	/**
	 * Transfer the content to a binary output stream.  If the content source
	 * is text, it is encoded by the Codec.  If the content source is binary, the raw bytes
	 * are transferred.
	 */
    public void write(OutputStream outputStream)
        throws IOException;

	/**
	 * Transfer the content to a character output stream.  If the content source
	 * is binary, this fails.
	 */
    public void write(Writer writer)
        throws IOException;
}
