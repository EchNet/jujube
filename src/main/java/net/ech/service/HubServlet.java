package net.ech.service;

import net.ech.config.*;
import net.ech.io.*;
import net.ech.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.ServletException;

/**
 * Generic servlet that runs some type of Controller.
 */
public class HubServlet
	extends HttpServlet
{
	private final static long A_LONG_WHILE = (long) (5 * 365.25 * 24 * 60 * 60);  // 5 years, in seconds

    @Override
	public void service(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        if ("OPTIONS".equals(request.getMethod())) {
            response.addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
            response.addHeader("Access-Control-Max-Age", "72000");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        }

		super.service(request, response);
	}

    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
    {
		doGetOrPost(request, response, false);
	}

    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
    {
		doGetOrPost(request, response, true);
	}

	private void doGetOrPost(HttpServletRequest request, HttpServletResponse response, boolean isPost)
		throws ServletException, IOException
	{
		long startTime = System.currentTimeMillis();

		try {
			writeResponse(createController(request).query(), isPost, response);
		}
		catch (FileNotFoundException e) {
			response.sendError(404, e.getMessage());
		}
		catch (RequestException e) {
			response.sendError(500, e.getMessage());
		}
		catch (IOException e) {
			drainError(request, e);
			throw e;
		}
		catch (ServletException e) {
			drainError(request, e);
			throw e;
		}
		catch (RuntimeException e) {
			drainError(request, e);
			throw e;
		}

		drainLogEntry(request, (int)(System.currentTimeMillis() - startTime));
	}

	/**
	 * Protected for the sake of unit tests.
	 */
	protected ContentQuery createController(HttpServletRequest request)
		throws ServletException, IOException
	{
		return getControllerFactory().createController(new RequestWrapper(request));
	}

	private DefaultControllerFactory getControllerFactory()
		throws ServletException, IOException
	{
		// My servlet name is the key to my configured ControllerFactory.
		return DefaultControllerFactory.byServletName(getConfiguration(), getServletName());
	}

    private Configuration getConfiguration()
    {
        return new ServletContextManager(getServletContext()).getConfiguration();
    }

	private void writeResponse(ContentHandle contentHandle, boolean isPost, HttpServletResponse response)
		throws IOException
	{
		if (contentHandle != null) {
			if (!isPost) {
				switch (contentHandle.getCacheAdvice()) {
				case CACHE_INDEFINITELY:
					writeCacheControlHeaders(A_LONG_WHILE, response);
					break;
				case DONT_CACHE:
					writeCacheControlHeaders(0, response);
					break;
				}
			}

			response.setContentType(contentHandle.getCodec().getContentType());

			if (contentHandle.getCodec().getCharacterEncoding() != null) {
				response.setCharacterEncoding(contentHandle.getCodec().getCharacterEncoding());
				contentHandle.write(response.getWriter());
			}
			else {
				contentHandle.write(response.getOutputStream());
			}
		}
	}

	private void writeCacheControlHeaders(long cachePeriod, HttpServletResponse response)
	{
		long date = System.currentTimeMillis();
		long expires = date;
		String cacheControl = "no-cache";

		if (cachePeriod > 0) {
			expires += cachePeriod * 1000;
			cacheControl = "public, max-age=" + cachePeriod;
		}

		response.setDateHeader("Date", date);
		response.setDateHeader("Expires", expires);
		response.setHeader("Cache-Control", cacheControl);
	}

	/**
	 * Log a servlet run - success case.
	 */
	private void drainLogEntry(HttpServletRequest request, int elapsedMillis)
	{
		drainIt(encodeRequestForLog(request).addEntry("elapsed", elapsedMillis), "serverDrain");
	}

	/**
	 * Log a servlet run - error case.
	 */
	private void drainError(HttpServletRequest request, Exception error)
	{
		drainIt(encodeRequestForLog(request).addEntry("error", encodeError(error)), "errorDrain");
	}

	private Hash encodeRequestForLog(HttpServletRequest request)
	{
		RequestWrapper requestWrapper = new RequestWrapper(request);

		return new Hash()
			.addEntry("sn", this.getServletName())
			.addEntry("url", request.getRequestURL().toString())
			.addEntry("ts", System.currentTimeMillis())
			.addEntry("headers", requestWrapper.getHeaderMap());
	}

	private Hash encodeError(Exception error)
	{
		return new Hash()
			.addEntry("message", error.getMessage())
			.addEntry("class", error.getClass().getName());
	}

	private void drainIt(Object obj, String drainName)
	{
		try {
			ContentDrain drain = getConfiguration().getBean(drainName, ContentDrain.class); // never null
			drain.accept(new JsonContentHandle(obj));
		}
		catch (IOException e) {
			// Sorry, end of the line.
		}
	}
}
