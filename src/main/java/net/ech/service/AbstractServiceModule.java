package net.ech.service;

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
	{
	}

	@Override
	public void process()
	{
	}

	@Override
	public void postprocess()
	{
	}
}
