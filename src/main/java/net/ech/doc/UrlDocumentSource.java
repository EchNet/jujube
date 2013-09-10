package net.ech.doc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

public class UrlDocumentSource
	implements DocumentSource
{
	private URL url;
	private URLConnection urlConnection;
	private String mimeType;

	public UrlDocumentSource(String key)
		throws IOException
	{
		this.url = new URL(key);
	}

	@Override
	public Reader open()
		throws IOException
	{
		urlConnection = url.openConnection();
		urlConnection.setConnectTimeout(5000);
		urlConnection.setReadTimeout(5000);
		if (urlConnection instanceof HttpURLConnection) {
			urlConnection.setRequestProperty("Accept-Encoding", "gzip");
			switch (((HttpURLConnection) urlConnection).getResponseCode()) {
			case 403:
			case 404:
				throw new FileNotFoundException(toString());
			}
		}

		mimeType = urlConnection.getContentType();
		String charSet = null;
		if (mimeType != null) {
			StringTokenizer tokens = new StringTokenizer(mimeType, ";");
			for (int tx = 0; tokens.hasMoreTokens(); ++tx) { 
				String token = tokens.nextToken().trim();
				switch (tx) {
				case 0: 
					mimeType = token;
					break;
				default:
					if (token.startsWith("charset=")) {
						charSet = token.substring(8);
					}
				}
			}
		}

		InputStream inputStream = urlConnection.getInputStream();
		String contentEncoding = urlConnection.getContentEncoding();
		if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
			inputStream = new GZIPInputStream(inputStream);
		}
		return new BufferedReader(new InputStreamReader(inputStream, charSet == null ? "UTF-8" : charSet));
	}

	@Override
	public String getMimeType()
		throws IOException
	{
		if (urlConnection == null) {
			throw new IllegalStateException();
		}
		return mimeType;
	}

	@Override
	public String toString()
	{
		return url.toString();
	}
}
