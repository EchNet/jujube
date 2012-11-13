package net.ech.service;

public class AbstractServiceModule
	extends ProxyServiceContext
	implements ServiceModule
{
	public AbstractServiceModule(ServiceContext context)
	{
		super(context);
	}

	@Override
	public void serviceStarted()
	{
	}

	@Override
	public void contentReceived()
	{
	}

	@Override
	public void contentReady()
	{
	}
}
