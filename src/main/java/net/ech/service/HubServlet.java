package net.ech.service;

import net.ech.config.Whence;
import net.ech.nio.ItemHandle;
import net.ech.nio.json.JsonCodec;
import net.ech.util.StrongReference;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class HubServlet
	extends HttpServlet
{
	private final static String SERVICE_CONFIG = "refapp/simple_proxy.json";  // TEMPORARILY HARDWIRED
	private final static String HUB_SERVICE_CONTEXT_ATTR = "hubServiceContext";

	private ServiceDefinition serviceDefinition;

	@Override
	public void init()
		throws ServletException
	{
		try {
			Object document = new JsonCodec().decode(new BufferedReader(new FileReader(SERVICE_CONFIG)));
			serviceDefinition = new Whence(document).pull("service", ServiceDefinition.class);
		}
		catch (IOException e) {
			throw new ServletException(e);
		}
	}

    @Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
    {
		try {
			try {
				super.service(request, response);
				postprocess(request);
			}
			catch (FileNotFoundException e) {
				getServiceContext(request).sendError(404, "not found");
				return;
			}
			catch (Exception e) {
				getServiceContext(request).sendError(500, "server error");
				throw e;
			}
			getServiceContext(request).handleResponseContent();
		}
		catch (Exception e) {
			logError(e);
		}
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException
	{
		doGet(request, response);
	}

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException
	{
		ServiceContext context = initServiceContext(request, response);

		for (ServiceModule serviceModule : getServiceDefinition().getServiceModules(context)) {
			serviceModule.process(context);
		}
	}

	private ServiceDefinition getServiceDefinition()
	{
		return serviceDefinition;
	}

	private AbstractServiceContext getServiceContext(final HttpServletRequest request)
	{
		return (AbstractServiceContext) request.getAttribute(HUB_SERVICE_CONTEXT_ATTR);
	}

	private AbstractServiceContext initServiceContext(final HttpServletRequest request, final HttpServletResponse response)
	{
		AbstractServiceContext serviceContext = new AbstractServiceContext() {

			@Override
			public HttpServlet getServlet()
			{
				return HubServlet.this;
			}

			@Override
			public HttpServletRequest getRequest()
			{
				return request;
			}

			@Override
			public HttpServletResponse getResponse()
			{
				return response;
			}
		};
		request.setAttribute(HUB_SERVICE_CONTEXT_ATTR, serviceContext);
		return serviceContext;
	}

	private void postprocess(final HttpServletRequest request)
		throws ServletException, IOException
	{
		for (ServicePostProcessor postProcessor : getServiceDefinition().getPostProcessors()) {
			postProcessor.postprocess(getServiceContext(request));
		}
	}

	private void logError(Exception error)
	{
		try {
			// TODO: implement real logging
			PrintWriter log = new PrintWriter(new BufferedWriter(new FileWriter("error.log", true)));
			try {
				log.write("*****\n" + new java.util.Date().toString() + "\n-\n");
				error.printStackTrace(log);
			}
			finally {
				log.close();
			}
		}
		catch (IOException e) {
		}
	}
}
