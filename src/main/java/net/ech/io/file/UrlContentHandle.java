package net.ech.io.file;

import net.ech.codec.*;
import net.ech.io.*;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

/**
 * Treat a URL as a ContentHandle.
 */
public class UrlContentHandle
	extends AbstractContentHandle
    implements ContentHandle
{
	private URL url;
	private Codec codec;
	private boolean isOverrideCodec;
	private InputStream inputStream;

	/**
	 * Constructor.  Treat the given URL as a ContentHandle.
	 */
	public UrlContentHandle(URL url)
		throws IOException
	{
		super(url.toString());
		this.url = url;
		open();
	}

	/**
	 * Constructor.  Treat the given URL as a ContentHandle, but override default treatment of its
	 * content with the given Codec.
	 */
	public UrlContentHandle(URL url, Codec codec)
		throws IOException
	{
		super(url.toString());
		this.url = url;
		this.codec = codec;
		this.isOverrideCodec = true;
		open();
	}

	public URL getUrl()
	{
		return url;
	}

	@Override
	public Codec getCodec()
		throws IOException
	{
		return codec;
	}

	@Override
    public Object getDocument()
        throws IOException
	{
		open();
		try {
			return codec.decode(inputStream);
		}
		finally {
			close();
		}
	}

	/**
	 * Stream output from URL connection, bypassing unnecessary decoding and encoding steps.
	 */
	@Override
    public void write(OutputStream outputStream)
        throws IOException
	{
		open();
		try {
			BinaryCodec.transferBytes(inputStream, outputStream);
		}
		finally {
			close();
		}
	}

	/**
	 * Stream output from URL connection to character output stream.
	 */
	@Override
    public void write(Writer writer)
        throws IOException
	{
		open();
		try {
			getCodec().write(inputStream, writer);
		}
		finally {
			close();
		}
	}

	private void open()
		throws IOException
	{
		if (this.inputStream == null) {
			try {
				URLConnection urlConnection = url.openConnection();
				if (urlConnection instanceof HttpURLConnection) {
					urlConnection.setRequestProperty("Accept-Encoding", "gzip");
					switch (((HttpURLConnection) urlConnection).getResponseCode()) {
					case 403:
					case 404:
						throw new FileNotFoundException(url.toString());
					}
				}
				if (!isOverrideCodec) {
					this.codec = findCodec(urlConnection.getContentType());
				}
				this.inputStream = urlConnection.getInputStream();
				final String contentEncoding = urlConnection.getContentEncoding();
				if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip"))
					this.inputStream = new GZIPInputStream(this.inputStream);
			}
			catch (FileNotFoundException e) {
				throw e;
			}
			catch (IOException e) {
				throw new IOException(url.toString(), e);
			}
		}
	}

	protected static Codec findCodec(String contentType)
		throws IOException
	{
		if (contentType == null) {
			return new BinaryCodec();
		}
		String charSet = null;
		StringTokenizer tokens = new StringTokenizer(contentType, ";");
		for (int tx = 0; tokens.hasMoreTokens(); ++tx) { 
			String token = tokens.nextToken().trim();
			switch (tx) {
			case 0: 
				contentType = token;
				break;
			default:
				if (token.startsWith("charset=")) {
					charSet = token.substring(8);
				}
			}
		}
		return ContentTypes.getDefaultCodec(contentType, charSet);
	}

	private void close()
		throws IOException
	{
		try {
			inputStream.close();
		}
		finally {
			inputStream = null;
		}
	}
}
