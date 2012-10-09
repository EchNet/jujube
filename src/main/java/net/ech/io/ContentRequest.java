package net.ech.io;

import java.util.*;

public class ContentRequest
{
	private String path;
	private Map<String,Object> parameters;
	private Map<String,Object> attributes;

	public ContentRequest(String path)
	{
		this.path = path;
		this.parameters = new HashMap<String,Object>();
		this.attributes = new HashMap<String,Object>();
	}

	public ContentRequest(String path, Map<String,Object> parameters)
	{
		this.path = path;
		this.parameters = new HashMap<String,Object>(parameters);
		this.attributes = new HashMap<String,Object>();
	}

	public ContentRequest(ContentRequest request)
	{
		this.path = request.path;
		this.parameters = new HashMap<String,Object>(request.parameters);
		this.attributes = request.attributes;
	}

	public String getPath()
	{
		return path;
	}

	public Object getParameter(String key)
	{
		return parameters.get(key);
	}

	public Map<String,Object> getParameters()
	{
		return Collections.unmodifiableMap(parameters);
	}

	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	public Map<String,Object> getAttributes()
	{
		return Collections.unmodifiableMap(attributes);
	}

	public ContentRequest withPath(String path)
	{
		ContentRequest other = new ContentRequest(this);
		other.path = path;
		return other;
	}

	public ContentRequest withParameters(Map<String,Object> parameters)
	{
		ContentRequest other = new ContentRequest(this);
		other.parameters = new HashMap<String,Object>(parameters);
		return other;
	}

	public void putAttribute(String name, Object value)
	{
		attributes.put(name, value);
	}

	public String toString()
	{
		String str = path;
		if (parameters.size() > 0) {
			str += "(" + parameters + ")";
		}
		return str;
	}
}
