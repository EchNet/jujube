package net.ech.service;

import net.ech.io.*;
import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

/**
 * Decorate an HTTP servlet request.
 */
public class RequestWrapper
	implements ContentQuery
{
    private HttpServletRequest request;
	private String path;

	public RequestWrapper(HttpServletRequest request)
	{
		this(request, getPathInfo(request));
	}

	public RequestWrapper(HttpServletRequest request, String path)
	{
		this.request = request;
		this.path = path;
	}

	public HttpServletRequest getRequest()
	{
		return request;
	}

	public boolean isPost()
	{
		return "POST".equals(request.getMethod());
	}

	final public String getRequestParameter(String name, String dflt)
	{
		String value = request.getParameter(name);
		return value == null ? dflt : value;
	}

	/**
	 * Get the content type, sans charset.
	 */
	public String getContentType()
	{
		String contentTypeString = request.getContentType();
		if (contentTypeString != null) {
			String[] tokens = contentTypeString.split(";");
			if (tokens.length > 0) {
				return tokens[0].trim();
			}
		}
		return null;
	}

	/**
	 * Get the path.
	 */
	public String getPath()
	{
		return path;
	}

	public String getFullPath()
	{
		return getPathInfo(request);
	}

	/**
	 * Get a hash of request parameters.
	 * If a parameter appears multiple times, its value in the hash is a list of strings.
	 * Otherwise, it is a string.
	 */
	public Map<String,Object> getParameterMap()
	{
		if (request.getAttribute("swoop.param.map") == null) {

			Map<String,Object> parameters = new LinkedHashMap<String,Object>();
			for (Map.Entry<String,String[]> paramEntries : ((Map<String,String[]>) getRequest().getParameterMap()).entrySet()) { 
				String[] paramValues = paramEntries.getValue();
				parameters.put(paramEntries.getKey(), paramValues.length == 1 ? (Object) paramValues[0] : (Object) Arrays.asList(paramValues));
			}

			request.setAttribute("swoop.param.map", parameters);
		}

		return (Map<String,Object>) request.getAttribute("swoop.param.map");
	}

	/**
	 * Get a hash of request parameters.
	 * If a header appears multiple times, its value in the hash is a list of strings.
	 * Otherwise, it is a string.
	 */
	public Map<String,Object> getHeaderMap()
	{
		if (request.getAttribute("swoop.header.map") == null) {

			Map<String,Object> result = new HashMap<String,Object>();

			for (String headerName : (List<String>) Collections.list(request.getHeaderNames())) {
				List<String> valueList = null;
				for (String headerValue : (List<String>) Collections.list(request.getHeaders(headerName))) {
					if (valueList == null) {
						result.put(headerName, headerValue);
						valueList = new ArrayList<String>();
					}
					if (valueList.size() > 0) {
						result.put(headerName, valueList);
					}
					valueList.add(headerValue);
				}
			}

			request.setAttribute("swoop.header.map", result);
		}

		return (Map<String,Object>) request.getAttribute("swoop.header.map");
	}

	/**
	 * Get request content as a ContentHandle.
	 */
	public ContentHandle getContentHandle()
		throws IOException
	{
		return
			ContentTypes.isForm(getContentType())
				? new JsonContentHandle(getParameterMap())
				: new RequestContentHandle(this);
	}

	/**
	 * ContentQuery interface - Get request content as a ContentHandle.
	 */
	@Override
	public ContentHandle query()
		throws IOException
	{
		return getContentHandle();
	}

	/**
	 * Get the original path info, factoring out JSP redirection, and cleaned up (leading slash removed).
	 */
	private static String getPathInfo(HttpServletRequest request)
	{
		String pathInfo = (String) request.getAttribute("javax.servlet.include.path_info");
		if (pathInfo == null) {
			pathInfo = request.getPathInfo();
		}
		if (pathInfo == null) {
			pathInfo = "";
		}
		if (pathInfo.startsWith("/")) {
			pathInfo = pathInfo.substring(1);
		}
		return pathInfo;
	}
}
