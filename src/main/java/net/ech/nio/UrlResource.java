package net.ech.nio;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class UrlResource
	extends UrlResourceConfig
	implements Resource
{
	public final static String DEFAULT_CHARACTER_ENCODING = "UTF-8";

	public static class Config
		extends UrlResourceConfig
	{
		public Config()
		{
		}

		public Config(String base)
			throws MalformedURLException
		{
			setBase(base);
		}

		public Config(URL base)
		{
			this.base = base;
		}

		public void setBase(String base)
			throws MalformedURLException
		{
			this.base = new URL(base);
		}

		public String getBase()
		{
			return base == null ? null : base.toString();
		}
	}

	public UrlResource(Config config)
	{
		super(config);
	}

	@Override
	public ItemHandle resolve(Query query)
		throws IOException
	{
		return new UrlItemHandle(new URL(base, query.getPath() + toQueryString(query)));
	}

	@Override
	public String toString()
	{
		return base.toString();
	}

	private String toQueryString(Query query)
		throws UnsupportedEncodingException
	{
		Map<String,Object> params = Query.parseQueryString(base.getQuery());
		params.putAll(query.getParameters());
		return Query.formQueryString(params);
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
