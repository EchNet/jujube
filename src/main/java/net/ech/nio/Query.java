package net.ech.nio;

import java.io.*;
import java.net.*;
import java.util.*;

public class Query
{
	private URI uri;
	private Map<String,Object> parameters;
	private Map<String,Object> attributes;

	public Query(String uriString)
		throws URISyntaxException
	{
		this(new URI(uriString));
	}

	public Query(URI uri)
	{
		this(uri, new HashMap<String,Object>(), new HashMap<String,Object>());
	}

	private Query(URI uri, Map<String,Object> parameters, Map<String,Object> attributes)
	{
		this.uri = uri;
		this.parameters = parameters;
		this.attributes = attributes;
	}

	public String getAuthority()
	{
		return uri.getAuthority();
	}

	public String getPath()
	{
		String path = uri.getPath();
		if (path.startsWith("/")) path = path.substring(1);
		return path;
	}

	public Map<String,Object> getParameters()
	{
		Map<String,Object> params = parseQueryString(uri.getRawQuery());
		params.putAll(parameters);
		return params;
	}

	public void putAttribute(String name, Object value)
	{
		attributes.put(name, value);
	}

	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	public Map<String,Object> getAttributes()
	{
		return Collections.unmodifiableMap(attributes);
	}

	public Query withPath(String path)
		throws URISyntaxException
	{
		return new Query(
			new URI(uri.getScheme(),
				uri.getAuthority(), path,
				uri.getQuery(), uri.getFragment()),
			parameters,
			attributes);
	}

	public Query withParameters(Map<String,Object> parameters)
	{
		return new Query(uri, new HashMap<String,Object>(parameters), attributes);
	}

	@Override
	public String toString()
	{
		// TODO: add parameters
		return uri.toString();
	}

	public static Map<String,Object> parseQueryString(String rawQueryString)
	{
		Map<String,Object> params = new LinkedHashMap<String,Object>();
		if (rawQueryString != null) {
			for (String paramString : rawQueryString.split("&")) {
				String name = paramString;
				String value = "";
				int eq = name.indexOf('=');
				if (eq >= 0) {
					name = name.substring(0, eq);
					value = paramString.substring(eq + 1);
				}
				if (params.containsKey(name)) {
					Object v = params.get(name);
					if (v instanceof List) {
						((List<String>) v).add(value);
					}
					else {
						List<String> list = new ArrayList<String>();
						list.add(v.toString());
						list.add(value);
						params.put(name, list);
					}
				}
				else {
					params.put(name, value);
				}
			}
		}
		return params;
	}

	public static String formQueryString(Map<String,Object> params)
		throws UnsupportedEncodingException
	{
		StringBuilder buf = new StringBuilder();

		for (Map.Entry<String,Object> entry : params.entrySet()) 
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
		return URLEncoder.encode(str, "utf-8");
	}
}
