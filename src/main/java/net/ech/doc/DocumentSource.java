package net.ech.doc;

import java.io.IOException;
import java.io.Reader;

/**
 * A document source encapsulates logic for resolving a document URI to a document
 * URL, and for opening an input stream from that location.  Stealing the "open 
 * stream" from the {@link java.net.URL} class enables implementations to report
 * error information that would otherwise be hidden by the {@link java.net.URL} 
 * class.  The chief example is the differentiation of {@link java.io.FileNotFoundException}
 * from security exception and other types of input exception.
 */
public interface DocumentSource
{
	/**
	 * Open an input stream to the document source.
	 */
	public Reader open()
		throws IOException;

	/**
	 * Get the MIME content type.  Must have previously opened this source.
	 */
	public String getMimeType()
		throws IOException;

	/**
	 * The location of the document, in human readable form. 
	 */
	public String toString();
}
