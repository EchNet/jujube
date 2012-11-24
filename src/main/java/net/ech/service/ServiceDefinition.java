package net.ech.service;

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

public class ServiceDefinition
{
	private ServiceModule[] serviceModules;
	private ServicePostProcessor[] postProcessors;

	public void setServiceModules(ServiceModule[] serviceModules)
	{
		this.serviceModules = serviceModules;
	}

	public ServiceModule[] getServiceModules()
	{
		return serviceModules;
	}

	public void setPostProcessors(ServicePostProcessor[] postProcessors)
	{
		this.postProcessors = postProcessors;
	}

	public ServicePostProcessor[] getPostProcessors()
	{
		return postProcessors;
	}

	public ServiceModule[] getServiceModules(ServiceContext serviceContext)
		throws IOException
	{
		HttpServletRequest request = serviceContext.getRequest();
		List<ServiceModule> list = new ArrayList<ServiceModule>();
		for (ServiceModule module : getServiceModules()) {
			if ((module.getMethod() == null || request.getMethod().matches(module.getMethod())) &&
				// TODO: respect path component boundaries
				(module.getPath() == null || request.getPathInfo().startsWith(module.getPath())))
			{
				if (module.getPath() != null) {
					serviceContext.setQueryPath(request.getPathInfo().substring(module.getPath().length()));
				}
				list.add(module);
				break;
			}
		}
		if (request.getMethod().matches("GET|POST") && list.size() == 0) {
			throw new FileNotFoundException(request.getPathInfo());
		}
		return list.toArray(new ServiceModule[list.size()]);
	}
}
