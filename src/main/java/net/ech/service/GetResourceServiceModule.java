package net.ech.service;

import java.io.IOException;
import javax.servlet.ServletException;
import net.ech.nio.Query;
import net.ech.nio.Resource;

public class GetResourceServiceModule
	extends AbstractServiceModule
	implements ServiceModule
{
	private Resource resource;

	public void setResource(Resource resource)
	{
		this.resource = resource;
	}

	@Override
	public void process(ServiceContext context)
		throws IOException, ServletException
	{
		try {
			String uri = context.getQueryPath();
			if (context.getRequest().getQueryString() != null) {
				uri += context.getRequest().getQueryString();
			}
			Query query = Query.fromUriString(uri);
			context.setContent(resource.resolve(query));
		}
		catch (java.net.URISyntaxException e) {
			throw new IOException(e);
		}
	}
}
