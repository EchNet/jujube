package net.ech.nio;

import java.io.*;

public interface ItemHandle
{
	/**
	 * @throws IllegalStateException if this item has already been presented
	 */
    public InputStream presentInputStream()
        throws IOException;

	/**
	 * @throws IllegalStateException if this item has already been presented
	 */
    public Reader presentReader()
        throws IOException;

	/**
	 * @throws IllegalStateException if this item has already been presented
	 */
    public Object/*TODO: define Document class */ presentDocument()
        throws IOException;

	/**
	 */
    public boolean isLatent();

	/**
	 */
    public Metadata getMetadata()
        throws IOException;

	/**
	 * A human-readable description of this item's source, e.g. a URI.  May not be null.
	 */
	public String toString();
}
