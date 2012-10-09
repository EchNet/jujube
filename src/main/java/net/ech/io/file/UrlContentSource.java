package net.ech.io.file;

import net.ech.io.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class UrlContentSource
	extends AbstractFileContentSource
	implements ContentSource
{
	private final static String CHAR_ENCODING = "UTF-8";

	private URL base;

	public UrlContentSource(URL base)
	{
		this.base = base;
	}

	public UrlContentSource(URL base, boolean mStatic)
	{
		super(mStatic);
		this.base = base;
	}

	@Override
    protected AbstractContentHandle resolveUri(URI uri, ContentRequest request)
        throws IOException
	{
		// If path is empty, use last component of base as path.
		String path = uri.getPath();
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.length() == 0) {
			String basePath = base.getPath();
			if (!basePath.endsWith("/")) {
				int ix = basePath.lastIndexOf('/');
				path = ix < 0 ? basePath : basePath.substring(ix + 1);
			}
		}

		URL url = new URL(base, processPath(path) + toQueryString(uri, request));
		return getCodec() != null ? new UrlContentHandle(url, getCodec()) : new UrlContentHandle(url);
	}

	public String toString()
	{
		return base.toString();
	}

    private String toQueryString(URI uri, ContentRequest request)
        throws UnsupportedEncodingException
    {
		StringBuilder buf = new StringBuilder();
		if (base.getQuery() != null) {
			buf.append("?");
			buf.append(base.getQuery());
		}
		if (uri.getQuery() != null) {
			buf.append(buf.length() == 0 ? "?" : "&");
			buf.append(uri.getQuery());
		}
		for (Map.Entry<String,Object> entry : request.getParameters().entrySet()) 
		{
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value == null) {
				value = "";
			}
			List<Object> valueList = (value instanceof List) ? ((List<Object>) value) : Collections.singletonList(value);
			for (Object v : valueList) {
				buf.append(buf.length() == 0 ? "?" : "&");
				buf.append(urlEncode(key));
				buf.append("=");
				buf.append(urlEncode(v.toString()));
			}
		}
		return buf.toString();
    }

    private static String urlEncode(String str) 
        throws UnsupportedEncodingException
    {
        return URLEncoder.encode(str, CHAR_ENCODING);
    }
}
