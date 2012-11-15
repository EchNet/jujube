package net.ech.service;

import net.ech.config.Whence;
import net.ech.nio.ItemHandle;
import net.ech.nio.json.JsonCodec;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class HubServlet
	extends HttpServlet
{
	private final static String SERVICE_CONFIG = "refapp/swoop_proxy.json";  // TEMPORARY

	private ServiceDefinition serviceDefinition;

    @Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
    {
		long startTime = System.currentTimeMillis();

		try {
			ServiceDefinition serviceDefinition = getServiceDefinition();

			// Turn it inside out.
			ServiceContext serviceContext = new ServiceContext()
			{
				private ItemHandle contentItemHandle;

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

				@Override
				public ItemHandle getContentItemHandle()
				{
					return contentItemHandle;
				}

				public void submitContent(ItemHandle contentItemHandle)
					throws ServletException
				{
					if (contentItemHandle != null) {
						throw new ServletException("");
					}
				}
			};

			ServiceModule[] serviceModules = serviceDefinition.filterServiceModules(request);

			for (ServiceModule serviceModule : serviceModules) {
				serviceModule.setServiceContext(serviceContext);
			}
			for (ServiceModule serviceModule : serviceModules) {
				serviceModule.preprocess();
			}
			for (ServiceModule serviceModule : serviceModules) {
				serviceModule.process();
			}
			for (ServiceModule serviceModule : serviceModules) {
				serviceModule.postprocess();
			}
		}
		catch (FileNotFoundException e) {
			sendError(response, 404, "not found");
		}
		catch (Exception e) {
			sendError(response, 500, "server error");
			// TODO: log it.
		}
	}

	private ServiceDefinition getServiceDefinition()
		throws IOException
	{
		if (serviceDefinition == null) {
			synchronized (this) {
				if (serviceDefinition == null) {
					Object document = new JsonCodec().decode(new BufferedReader(new FileReader(SERVICE_CONFIG)));
					serviceDefinition = new Whence(document).pull("$service", ServiceDefinition.class);
				}
			}
		}
		return serviceDefinition;
	}

	private void sendError(HttpServletResponse response, int statusCode, String message)
	{
		try {
			response.sendError(statusCode, message);
		}
		catch (IOException ignore) {
		}
	}
}
