package net.ech.service;

import java.io.IOException;
import java.util.*;
import net.ech.config.Whence;
import javax.servlet.http.HttpServletRequest;

public class ServiceDefinition
{
	public static class ServiceModuleMatcher
	{
		private String method;
		private String path;
		private ServiceModule module;

		public String getMethod()
		{
			return method;
		}

		public void setMethod(String method)
		{
			this.method = method;
		}

		public String getPath()
		{
			return path;
		}

		public void setPath(String path)
		{
			this.path = path;
		}
	}

	private Map[] modules;

	public void setModules(Map[] modules)
	{
		this.modules = modules;
	}

	public Map[] getModules()
	{
		return modules;
	}

	public ServiceModule[] filterServiceModules(HttpServletRequest request)
		throws IOException
	{
		List<ServiceModule> list = new ArrayList<ServiceModule>();
		for (Map<String,Object> smMatcher : modules) {
			if (smMatcher.containsKey("method") && !request.getMethod().matches(smMatcher.get("method").toString()))
				continue;
			// TODO: respect path component boundaries
			if (smMatcher.containsKey("path") && !request.getPathInfo().startsWith(smMatcher.get("path").toString()))
				continue;
			ServiceModule serviceModule = new Whence(smMatcher).pull("module", ServiceModule.class);
			if (smMatcher.containsKey("path")) {
				serviceModule.setModulePath(smMatcher.get("path").toString());
				serviceModule.setQueryPath(request.getPathInfo().substring(smMatcher.get("path").toString().length()));
			}
			list.add(serviceModule);
		}
		return list.toArray(new ServiceModule[list.size()]);
	}
}
