package net.ech.codec;

import java.io.*;

public interface Codec
{
	/**
	 * MIME type.
	 */
	public String getContentType();

	/**
	 * Get this Codec's character encoding, if applicable.
	 * @return a character encoding string (e.g. "UTF-8") or null if not applicable.
	 */
	public String getCharacterEncoding();

	public Object decode(InputStream inputStream)
		throws IOException;

	public void encode(Object obj, OutputStream outputStream)
		throws IOException;

	public void encode(Object obj, Writer writer)
		throws IOException;

	public void write(InputStream inputStream, Writer writer)
		throws IOException;
}
