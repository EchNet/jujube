package net.ech.service;

import net.ech.config.*;
import net.ech.io.*;
import net.ech.util.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.mock.web.*;

public class MockServiceBlock
{
	public final static String SERVLET_NAME = "$TEST";

	public MockServletConfig servletConfig = new MockServletConfig();
    public MockerHttpServletRequest request = new MockerHttpServletRequest();
    public MockHttpServletResponse response = new MockHttpServletResponse();
	private Hash configDoc = new Hash();
	public RiggedHubServlet servlet;
	private ContentQuery controller;
	private boolean initDone;

	private void init()
	{
		new ServletContextManager(servletConfig).putConfiguration(new Configuration(new ContentHandleRef(new JsonContentHandle(configDoc))));
	}

	public MockServiceBlock()
	{
		init();
		this.servlet = new RiggedHubServlet();
	}

	public MockServiceBlock(Hash controllerConfig)
	{
		this();
		this.configDoc.addEntry(SERVLET_NAME, controllerConfig);
	}

	public ContentQuery getController()
	{
		return controller;
	}

	public void setConfigDoc(Hash configDoc)
	{
		this.configDoc.putAll(configDoc);
	}

	public ServletContextManager getServletContextManager()
	{
		return new ServletContextManager(servletConfig);
	}

	public Configuration getConfiguration()
	{
		return getServletContextManager().getConfiguration();
	}

	public void initServlet()
		throws ServletException
	{
		if (initDone) {
			throw new IllegalStateException("repeated call to initServlet()");
		}
		initDone = true;
		servlet.init(servletConfig);
	}

    public void runServlet()
        throws ServletException, IOException
    {
		if (!initDone) {
			initServlet();
		}
		servlet.service(request, response);
    }

	public class RiggedHubServlet
		extends HubServlet
	{
		public RiggedHubServlet()
		{
		}

		@Override
		public String getServletName()
		{
			return SERVLET_NAME;
		}

		@Override
		public ContentQuery createController(HttpServletRequest request)
			throws IOException, ServletException
		{
			controller = super.createController(request);
			return controller;
		}
	}

	// Extend MockHttpServletRequest to allow mocking of getRequestURL()
	public class MockerHttpServletRequest
		extends MockHttpServletRequest
	{
		private String url = "http://localhost:8080/test";

		MockerHttpServletRequest()
		{
			setMethod("GET");
		}

		public void setURL(String url)
		{
			this.url = url;
		}

		public StringBuffer getRequestURL()
		{
			return new StringBuffer(url);
		}
	}
}
