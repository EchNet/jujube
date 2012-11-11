package net.ech.nio;

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
		Map<String,Object> params = parseQueryString();
		params.putAll(parameters);
		return Collections.unmodifiableMap(params);
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

	private Map<String,Object> parseQueryString()
	{
		Map<String,Object> params = new HashMap<String,Object>();
		for (String paramString : uri.getRawQuery().split("&")) {
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
		return params;
	}
}
