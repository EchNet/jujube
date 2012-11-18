package net.ech.nio;

import java.io.*;
import java.net.*;
import java.util.*;
import net.ech.util.KeyValuePair;

/**
 * A Query, like a URI or URL, consists of a number of components, and may be 
 * encoded as a String.  Unlike a URI or URL, 
 *
 * Class Query offers methods for manipulating the components of the URI beyond
 * what class URI offers.
 */
public class Query
{
	private String authority;
	private String path;
	private List<KeyValuePair<String,String>> parameters;
	private Map<String,Object> attributes = new HashMap<String,Object>();

	public static Query fromUriString(String uriString)
		throws URISyntaxException
	{
		URI uri = new URI(uriString);
		return new Query(
			uri.getAuthority(),
			uri.getPath(), 
			uri.getRawQuery());
	}

	public static List<KeyValuePair<String,String>> parseQueryString(String rawQueryString)
	{
		List<KeyValuePair<String,String>> parameters = new ArrayList<KeyValuePair<String,String>>();
		if (rawQueryString != null) {
			if (rawQueryString.startsWith("?")) {
				rawQueryString = rawQueryString.substring(1);
			}
			for (String paramString : rawQueryString.split("&")) {
				String name = paramString;
				String value = null;
				int eq = name.indexOf('=');
				if (eq >= 0) {
					name = name.substring(0, eq);
					value = urlDecode(paramString.substring(eq + 1));
				}
				parameters.add(new KeyValuePair<String,String>(urlDecode(name), value));
			}
		}
		return parameters;
	}

	public static String encodeQueryParameters(List<KeyValuePair<String,String>> parameters)
	{
		StringBuilder buf = new StringBuilder();
		for (KeyValuePair<String,String> parameter : parameters) {
			buf.append(buf.length() == 0 ? "?" : "&");
			buf.append(urlEncode(parameter.key));
			if (parameter.value != null) {
				buf.append("=");
				buf.append(urlEncode(parameter.value));
			}
		}
		return buf.toString();
	}

	public Query(String authority, String path, String query)
	{
		this.authority = authority;
		setPath(path);
		this.parameters = parseQueryString(query);
	}

	public void setAuthority(String authority)
	{
		this.authority = authority;
	}

	public String getAuthority()
	{
		return authority;
	}

	public void setPath(String path)
	{
		if (path != null && path.startsWith("/")) {
			path = path.substring(1);
		}
		this.path = path;
	}

	public String getPath()
	{
		return path;
	}

	public void setQuery(String query)
	{
		this.parameters = parseQueryString(query);
	}

	public String getQuery()
	{
		return encodeQueryParameters(this.parameters);
	}

	public void removeParameter(String key)
	{
		Iterator<KeyValuePair<String,String>> parametersIter = parameters.iterator();
		while (parametersIter.hasNext()) {
			KeyValuePair<String,String> param = parametersIter.next();
			if (key.equals(param.key)) {
				parametersIter.remove();
			}
		}
	}

	public void setParameter(String key, String value)
	{
		setParameterValues(key, Collections.singletonList(value));
	}

	public void setParameterValues(String key, List<String> values)
	{
		Iterator<String> valuesIter = values.iterator();
		Iterator<KeyValuePair<String,String>> parametersIter = parameters.iterator();
		while (parametersIter.hasNext()) {
			KeyValuePair<String,String> parameter = parametersIter.next();
			if (key.equals(parameter.key)) {
				if (valuesIter.hasNext()) {
					parameter.value = valuesIter.next();
				}
				else {
					parametersIter.remove();
				}
			}
		}
		addParameterValues(key, valuesIter);
	}

	public void addParameter(String key, String value)
	{
		parameters.add(new KeyValuePair<String,String>(key, value));
	}

	public void addParameterValues(String key, List<String> values)
	{
		addParameterValues(key, values.iterator());
	}
	
	private void addParameterValues(String key, Iterator<String> valuesIter)
	{
		while (valuesIter.hasNext()) {
			addParameter(key, valuesIter.next());
		}
	}

	public List<String> getParameterKeys()
	{
		List<String> keys = new ArrayList<String>();
		Iterator<KeyValuePair<String,String>> parametersIter = parameters.iterator();
		while (parametersIter.hasNext()) {
			KeyValuePair<String,String> parameter = parametersIter.next();
			keys.add(parameter.key);
		}
		return keys;
	}

	public String getParameter(String key)
	{
		Iterator<KeyValuePair<String,String>> parametersIter = parameters.iterator();
		while (parametersIter.hasNext()) {
			KeyValuePair<String,String> parameter = parametersIter.next();
			if (key.equals(parameter.key)) {
				return parameter.value;
			}
		}
		return null;
	}

	public List<String> getParameterValues(String key)
	{
		List<String> values = new ArrayList<String>();
		Iterator<KeyValuePair<String,String>> parametersIter = parameters.iterator();
		while (parametersIter.hasNext()) {
			KeyValuePair<String,String> parameter = parametersIter.next();
			if (key.equals(parameter.key)) {
				values.add(parameter.value);
			}
		}
		return values;
	}

	public void putAttribute(String key, Object value)
	{
		attributes.put(key, value);
	}

	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	public Map<String,Object> getAttributes()
	{
		return Collections.unmodifiableMap(attributes);
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		if (authority != null) {
			buf.append("//");
			buf.append(authority);
		}
		if (path != null) {
			if (buf.length() > 0) {
				buf.append("/");
			}
			buf.append(path);
		}
		buf.append(encodeQueryParameters(parameters));
		return buf.toString();
	}

	private static String urlEncode(String str) 
	{
		try {
			return URLEncoder.encode(str, "utf-8");
		}
		catch (UnsupportedEncodingException e) {
			// Should not be reached.
			throw new RuntimeException("utf-8 not supported??");
		}
	}

	private static String urlDecode(String str) 
	{
		try {
			return URLDecoder.decode(str, "utf-8");
		}
		catch (UnsupportedEncodingException e) {
			// Should not be reached.
			throw new RuntimeException("utf-8 not supported??");
		}
	}
}
