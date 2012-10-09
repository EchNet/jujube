package net.ech.service;

import net.ech.config.Configuration;
import net.ech.io.*;
import net.ech.util.*;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;
import javax.servlet.ServletException;

/**
 * Create a Controller instance for handling the given request.
 */
public class DefaultControllerFactory
{
	private Configuration configuration;
	private DQuery configDoc;

	public static DefaultControllerFactory byServletName(Configuration configuration, String servletName)
		throws IOException
	{
		return new DefaultControllerFactory(configuration, new DQuery(configuration.getContent().getDocument()).find(new DPath(servletName)));
	}

	public DefaultControllerFactory(Configuration configuration, DQuery configDoc)
	{
		this.configuration = configuration;
		this.configDoc = configDoc;
	}

	public ContentQuery createController(RequestWrapper requestWrapper)
		throws ServletException, IOException
	{
		return new Controller(requestWrapper);
	}

	private class Controller
		implements ContentQuery
	{
		RequestWrapper requestWrapper;
		DQuery controllerConfig;

		public Controller(RequestWrapper requestWrapper)
			throws IOException, ServletException
		{
			this.requestWrapper = requestWrapper;
			init();
		}

		private void init()
			throws IOException, ServletException
		{
			final DQuery[] controllerConfigs = new DQuery[1];
			configDoc.each(new DHandler() {
				public void handle(DQuery child) throws IOException {
					Pattern pattern = Pattern.compile(child.getPath().getLast().toString());
					Matcher matcher = pattern.matcher(requestWrapper.getPath());
					if (matcher.matches()) {
						controllerConfigs[0] = child;
					}
				}
			});
			if (controllerConfigs[0] == null) {
				throw new ServletException(requestWrapper.getFullPath() + ": bad path");
			}

			this.controllerConfig = controllerConfigs[0];

			if (requestWrapper.isPost() && controllerConfig.find("chute").isNull()) {
				throw new IOException(requestWrapper.getFullPath() + ": POST not supported");
			}

			if (!requestWrapper.isPost() && controllerConfig.find("source").isNull()) {
				throw new IOException(requestWrapper.getFullPath() + ": GET not supported");
			}
		}

		@Override
		public ContentHandle query()
			throws IOException
		{
			if (requestWrapper.isPost()) {
				ContentDrain contentDrain = new ContentDrainBuilder(configuration).build(configDoc.find("chute"));
				ContentHandle contentHandle = requestWrapper.getContentHandle();
				return contentDrain.accept(contentHandle);
			}
			else {
				ContentRequest contentRequest = ContentRequestManager.createContentRequest(requestWrapper, configuration);
				ContentSource contentSource = new ContentSourceBuilder(configuration).build(controllerConfig.find("source"));
				return contentSource.resolve(contentRequest);
			}
		}
	}
}
