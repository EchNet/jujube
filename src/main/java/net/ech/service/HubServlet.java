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

	private ServiceDefinition serviceDefinition;

    @Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
    {
		long startTime = System.currentTimeMillis();
		final StrongReference<ItemHandle> contentItemRef = new StrongReference<ItemHandle>();

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
				public void submitContent(ItemHandle contentItemHandle)
					throws ServletException
				{
					if (contentItemRef.get() != null) {
						throw new ServletException("service configuration error: multiple content submissions");
					}
					contentItemRef.set(contentItemHandle);
				}
			};

			ServiceModule[] serviceModules = serviceDefinition.filterServiceModules(request);

			for (ServiceModule serviceModule : serviceModules) {
				serviceModule.setServiceContext(serviceContext);
				serviceModule.preprocess();
			}

			if (contentItemRef.get() != null) {
				for (ServiceModule serviceModule : serviceModules) {
					serviceModule.postprocess(contentItemRef.get());
				}
				moveContent(contentItemRef.get(), serviceContext);
			}
		}
		catch (FileNotFoundException e) {
			sendError(response, 404, "not found");
		}
		catch (Exception e) {
			sendError(response, 500, "server error");
			logError(e);
		}
	}

	private ServiceDefinition getServiceDefinition()
		throws IOException
	{
		if (serviceDefinition == null) {
			synchronized (this) {
				if (serviceDefinition == null) {
					Object document = new JsonCodec().decode(new BufferedReader(new FileReader(SERVICE_CONFIG)));
					serviceDefinition = new Whence(document).pull("service", ServiceDefinition.class);
				}
			}
		}
		return serviceDefinition;
	}

	private void moveContent(ItemHandle item, ServiceContext serviceContext)
		throws IOException
	{
		ContentServiceModule csModule = new ContentServiceModule();
		csModule.setServiceContext(serviceContext);
		csModule.postprocess(item);
	}

	private void sendError(HttpServletResponse response, int statusCode, String message)
	{
		try {
			response.sendError(statusCode, message);
		}
		catch (IOException ignore) {
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
