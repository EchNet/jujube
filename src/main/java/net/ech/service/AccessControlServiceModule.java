package net.ech.service;

public class AccessControlServiceModule
	extends AbstractServiceModule
	implements ServiceModule
{
	public AccessControlServiceModule(ServiceContext context)
	{
		super(context);
	}

	@Override
	public void serviceStarted()
	{
        getResponse().addHeader("Access-Control-Allow-Origin", "*");

		if ("OPTIONS".equals(getRequest().getMethod())) {
			getResponse().addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
			getResponse().addHeader("Access-Control-Max-Age", "72000");
			getResponse().addHeader("Access-Control-Allow-Headers", "Content-Type");
		}
	}
}
