package net.ech.service;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import net.ech.nio.ItemHandle;

public class HubService
	implements ServiceContext, Runnable
{
	private HttpServlet servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private long startTime = System.currentTimeMillis();
	private ItemHandle contentItemHandle;

	public HubService(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response)
    {
		this.servlet = servlet;
		this.request = request;
		this.response = response;
	}

	@Override
	public HttpServlet getServlet()
	{
		return servlet;
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

	@Override
	public void run()
	{
		List<ServiceModule> serviceModules = new ArrayList<ServiceModule>();
		serviceModules.add(new AccessControlServiceModule(this));
		serviceModules.add(new CacheControlServiceModule(this));
		serviceModules.add(new ContentServiceModule(this));
		serviceModules.add(new PostServiceModule(this));
		serviceModules.add(new GetServiceModule(this));

		for (ServiceModule serviceModule : serviceModules) {
			serviceModule.serviceStarted();
		}
		if (contentItemHandle != null) {
			for (ServiceModule serviceModule : serviceModules) {
				serviceModule.contentReceived();
			}
			for (ServiceModule serviceModule : serviceModules) {
				serviceModule.contentReady();
			}
		}
	}
}
