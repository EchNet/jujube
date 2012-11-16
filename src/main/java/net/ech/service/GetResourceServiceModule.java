package net.ech.service;

import net.ech.nio.*;
import java.io.IOException;
import javax.servlet.ServletException;

public class GetResourceServiceModule
	extends AbstractServiceModule
	implements ServiceModule
{
	private Resource resource;

	public static class Config {
		private Resource resource;
		public void setResource(Resource resource) {
			this.resource = resource;
		}
		public Resource getResource() {
			return resource;
		}
	}

	public GetResourceServiceModule(Config config)
	{
		this.resource = config.resource;
	}

	@Override
	public void preprocess()
		throws IOException, ServletException
	{
		try {
			submitContent(resource.resolve(makeQuery()));
		}
		catch (java.net.URISyntaxException e) {
			throw new IOException(e);
		}
	}

	private Query makeQuery()
		throws java.net.URISyntaxException
	{
		String uri = getRequest().getRequestURI();
		if (getRequest().getQueryString() != null) {
			uri += getRequest().getQueryString();
		}
		return new Query(uri);
	}
}
