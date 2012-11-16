package net.ech.service;

import java.io.IOException;
import javax.servlet.ServletException;
import net.ech.nio.ItemHandle;

public class AbstractServiceModule
	extends ProxyServiceContext
	implements ServiceModule
{
	@Override
	public void setServiceContext(ServiceContext context)
	{
		setInner(context);
	}

	@Override
	public void preprocess()
		throws IOException, ServletException
	{
	}

	@Override
	public void postprocess(ItemHandle contentItemHandle)
		throws IOException, ServletException
	{
	}
}
