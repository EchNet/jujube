package net.ech.nio;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class UrlResource
	implements Resource
{
	public final static String DEFAULT_CHARACTER_ENCODING = "UTF-8";

	public static class Config
	{
		private String base;

		public Config()
		{
		}

		public Config(String base)
		{
			setBase(base);
		}

		public void setBase(String base)
		{
			this.base = base;
		}

		public String getBase()
		{
			return base;
		}
	}

	private URL baseUrl;
	private String baseQuery;

	public UrlResource(Config config)
		throws MalformedURLException
	{
		URL url = new URL(config.base);
		this.baseUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
		this.baseQuery = url.getQuery();
	}

	@Override
	public ItemHandle resolve(Query query)
		throws IOException
	{
		return new UrlItemHandle(formUrl(query)); 
	}

	@Override
	public String toString()
	{
		String str = baseUrl.toString();
		if (baseQuery != null) {
			str += baseQuery;
		}
		return str;
	}

	private URL formUrl(Query query)
		throws MalformedURLException
	{
		Query compQuery = new Query(null, query.getPath(), baseQuery);
		for (String paramKey : query.getParameterKeys()) {
			compQuery.setParameterValues(paramKey, query.getParameterValues(paramKey));
		}
		return new URL(baseUrl, compQuery.toString());
	}

	private class UrlItemHandle
		extends AbstractItemHandle
		implements ItemHandle
	{
		private URL url;
		private URLConnection urlConnection;
		private boolean opened;
		private String mimeType;
		private String characterEncoding;

		/**
		 * Constructor.  Treat the given URL as a ItemHandle.
		 */
		public UrlItemHandle(URL url)
			throws IOException
		{
			init(url);
			setContentType();
		}

		@Override
		public InputStream openInputStream()
			throws IOException
		{
			synchronized (this) {
				if (opened) {
					throw new IllegalStateException("already opened");
				}
				opened = true;
			}

			InputStream inputStream = urlConnection.getInputStream();
			String contentEncoding = urlConnection.getContentEncoding();
			if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
				inputStream = new GZIPInputStream(inputStream);
			}
			return inputStream;
		}

		@Override
		public Reader openReader()
			throws IOException
		{
			return new InputStreamReader(openInputStream(), characterEncoding);
		}

		@Override
		public Metadata getMetadata()
		{
			return new AbstractMetadata() {

				@Override
				public String getMimeType()
				{
					return mimeType;
				}

				@Override
				public String getCharacterEncoding()
				{
					return characterEncoding;
				}
			};
		}

		@Override
		public String toString()
		{
			return url.toString();
		}

		private void init(URL url)
			throws IOException
		{
			this.url = url;
			this.urlConnection = url.openConnection();
			handleHttp();
		}

		private void handleHttp()
			throws IOException
		{
			if (urlConnection instanceof HttpURLConnection) {

				urlConnection.setRequestProperty("Accept-Encoding", "gzip");

				int responseCode = ((HttpURLConnection) urlConnection).getResponseCode();
				switch (responseCode) {
				case 200:
					return;
				//case 403:
				case 404:
					throw new FileNotFoundException(url.toString());
				default:
					throw new IOException(url.toString() + ": HTTP status " + responseCode);
				}
			}
		}

		private void setContentType()
		{
			mimeType = urlConnection.getContentType();
			if (mimeType != null) {
				int tx = 0;
				for (String token : mimeType.split(";")) {
					token = token.trim();
					switch (tx) {
					case 0: 
						mimeType = token;
						break;
					default:
						if (token.startsWith("charset=")) {
							characterEncoding = token.substring(8);
						}
					}
					++tx;
				}

				if (characterEncoding == null && MimeType.getMimeType(mimeType).isText()) {
					characterEncoding = DEFAULT_CHARACTER_ENCODING;
				}
			}
		}
	}
}
