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

public class UrlDocumentProducer
	extends StreamDocumentProducer
	implements DocumentProducer
{
	public final static String JSON_CONTENT_TYPE = "";

	public UrlDocumentProducer(JsonDeserializer json, String source)
	{
		super(json, source);
	}

	@Override
	protected Reader openReader()
		throws IOException
	{
		URL url = new URL(getSource());
		URLConnection urlConnection = url.openConnection();
		urlConnection.setConnectTimeout(5000);
		urlConnection.setReadTimeout(5000);
		if (urlConnection instanceof HttpURLConnection) {
			urlConnection.setRequestProperty("Accept-Encoding", "gzip");
			switch (((HttpURLConnection) urlConnection).getResponseCode()) {
			case 403:
			case 404:
				throw new FileNotFoundException(getSource());
			}
		}

		String contentType = urlConnection.getContentType();
		String charSet = null;
		if (contentType != null) {
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
			if (!JSON_CONTENT_TYPE.equals(contentType)) {
				throw new IOException(getSource() + ": expected JSON but found content type " + contentType);
			}
		}

		InputStream inputStream = urlConnection.getInputStream();
		String contentEncoding = urlConnection.getContentEncoding();
		if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
			inputStream = new GZIPInputStream(inputStream);
		}
		return new BufferedReader(new InputStreamReader(inputStream, charSet == null ? "UTF-8" : charSet));
	}
}
