package net.ech.nio;

import java.io.*;

public interface ItemHandle
{
	/**
	 * @throws IllegalStateException if this handle is no longer latent and a stream has already been opened
	 */
    public InputStream openInputStream()
        throws IOException;

	/**
	 * @throws IllegalStateException if this handle is no longer latent and a stream has already been opened
	 */
    public Reader openReader()
        throws IOException;

	/**
	 */
    public Metadata getMetadata()
        throws IOException;

	/**
	 * A human-readable description of this item's source, e.g. a URI.  May not be null.
	 */
	public String toString();
}
