package net.ech.service;

import net.ech.io.*;
import net.ech.config.*;
import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

public class ContentRequestManager
{
	public final static String REQUEST = "request";
	public final static String CONFIGURATION = "configuration";

	private ContentRequest contentRequest;

	/**
	 * Factory method for ContentRequest.  Converts the given RequestWrapper to a ContentRequest, which it embellishes 
	 * with parameter aliases and context attributes.
	 */
	public static ContentRequest createContentRequest(RequestWrapper requestWrapper, Configuration configuration)
		throws IOException
	{
		String path = requestWrapper.getPath();
		Map<String,Object> parameters = new HashMap<String,Object>(requestWrapper.getParameterMap());

		if (requestWrapper.getRequestParameter("fmt", "jsonp").equals("jsonp")) {
			parameters.put("callback", requestWrapper.getRequestParameter("callback", "callback"));
		}

		ContentRequest contentRequest = new ContentRequest(path, parameters);
		contentRequest.putAttribute(REQUEST, requestWrapper.getRequest());
		contentRequest.putAttribute(CONFIGURATION, configuration);
		return contentRequest;
	}

	public ContentRequestManager(ContentRequest contentRequest)
	{
		this.contentRequest = contentRequest;
	}

	public ContentRequest getContentRequest()
	{
		return contentRequest;
	}

	public HttpServletRequest getRequest()
	{
		return (HttpServletRequest) contentRequest.getAttribute(REQUEST);
	}

	public RequestWrapper getRequestWrapper()
	{
		return new RequestWrapper(getRequest());
	}

	public Configuration getConfiguration()
	{
		return (Configuration) contentRequest.getAttribute(CONFIGURATION);
	}
}
