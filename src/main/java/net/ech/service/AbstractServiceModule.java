package net.ech.service;

import java.io.IOException;
import javax.servlet.ServletException;

public abstract class AbstractServiceModule
	implements ServiceModule
{
	private String method;
	private String path;

	@Override
	public String getMethod()
	{
		return method;
	}

	@Override
	public void setMethod(String method)
	{
		this.method = method;
	}

	@Override
	public String getPath()
	{
		return path;
	}

	@Override
	public void setPath(String path)
	{
		this.path = path;
	}

	@Override
	public void process(ServiceContext context)
		throws IOException, ServletException
	{
	}
}
