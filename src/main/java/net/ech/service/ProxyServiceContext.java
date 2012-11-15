package net.ech.service;

import javax.servlet.http.*;
import net.ech.nio.ItemHandle;

public class ProxyServiceContext
	implements ServiceContext
{
	private ServiceContext inner;

	public void setInner(ServiceContext inner)
	{
		this.inner = inner;
	}

	public HttpServlet getServlet()
	{
		return inner.getServlet();
	}

	public HttpServletRequest getRequest()
	{
		return inner.getRequest();
	}

	public HttpServletResponse getResponse()
	{
		return inner.getResponse();
	}

	public ItemHandle getContentItemHandle()
	{
		return inner.getContentItemHandle();
	}
}
