package net.ech.service;

public class AccessControlPostProcessor
	implements ServicePostProcessor
{
	@Override
	public void postprocess(ServiceContext context)
	{
        context.getResponse().addHeader("Access-Control-Allow-Origin", "*");

		if ("OPTIONS".equals(context.getRequest().getMethod())) {
			context.getResponse().addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
			context.getResponse().addHeader("Access-Control-Max-Age", "72000");
			context.getResponse().addHeader("Access-Control-Allow-Headers", "Content-Type");
		}
	}
}
