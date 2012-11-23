package net.ech.service;

import net.ech.nio.*;
import java.io.IOException;
import javax.servlet.ServletException;

public class GetResourceServiceModule
	extends AbstractServiceModule
	implements ServiceModule
{
	private Resource resource;
	private String queryPath;

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
	public void setQueryPath(String queryPath)
	{
		this.queryPath = queryPath;
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
		String uri = queryPath;
		if (getRequest().getQueryString() != null) {
			uri += getRequest().getQueryString();
		}
		return Query.fromUriString(uri);
	}
}
