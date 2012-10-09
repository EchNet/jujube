package net.ech.service;

import net.ech.config.*;
import net.ech.io.*;
import net.ech.io.file.*;
import net.ech.io.mongo.*;
import net.ech.io.template.*;
import net.ech.util.*;
import net.ech.mongo.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;

/**
 * A type-safe wrapper around the servlet context namespace. 
 */
public class ServletContextManager
{
	// Singleton instance of class MongoPool
	private static MongoPool mongoPool = new MongoPool();

	/**
	 * Pre-defined key.
	 */
	public final static String CONFIGURATION = "$CONFIGURATION$";

    private ServletContext servletContext;

	public ServletContextManager(ServletConfig servletConfig)
	{
		this(servletConfig.getServletContext());
    }

	public ServletContextManager(ServletContext servletContext)
	{
        this.servletContext = servletContext;
    }

    /**
     * Public only for the convenience of unit tests.
     */
    public void putConfiguration(Configuration configuration)
    {
		installDefaultBuilders(configuration);
		servletContext.setAttribute(CONFIGURATION, configuration);
	}

    /**
     * Get the group of shared objects defined by configuration properties.
     */
    public Configuration getConfiguration()
    {
		if (servletContext.getAttribute(CONFIGURATION) == null) {
			synchronized (servletContext) {
				if (servletContext.getAttribute(CONFIGURATION) == null) {
					String configFilePath =  ServiceProperties.getInstance().getConfigFilePath();
					putConfiguration(new Configuration(createConfigDocSource(configFilePath)));
				}
			}
		}
		return (Configuration) servletContext.getAttribute(CONFIGURATION);
    }

	// Override-able for unit testing.
	protected ContentQuery createConfigDocSource(String configFilePath)
	{
		CompositeDocument configDocument = new CompositeDocument();

		for (String sourceString : configFilePath.split(";")) {
			ContentQuery source;
			if (sourceString.indexOf(':') > 0) {
				// URI
				if (sourceString.startsWith("mongo")) {
					source = parseMongoSource(sourceString);
				}
				else {
					source = parseUrlSource(sourceString);
				}
			}
			else {
				// Path
				source = parsePathSource(sourceString);
			}
			configDocument.addSource(source);
		}

		return configDocument;
	}

	private ContentQuery parseMongoSource(String sourceString)
	{
		String[] pieces = sourceString.split("#");
		MongoCollection mongoCollection = mongoPool.createCollection(pieces[0]);
		MongoQueryContentSource mongoContentSource = new MongoQueryContentSource(mongoCollection);
		mongoContentSource.setPathFields(new String[]{ "_id" });
		CachingContentSource contentSource = new CachingContentSource(mongoContentSource);
		contentSource.setCacheSize(1);
		contentSource.setFreshness(15000);
		return new ContentSourceQuery(contentSource, new ContentRequest(pieces.length > 1 ? pieces[1] : ""));
	}

	private ContentQuery parseUrlSource(String sourceString)
	{
		try
		{
			UrlContentSource contentSource = new UrlContentSource(new URL(sourceString));
			return new ContentSourceQuery(contentSource, new ContentRequest(""));
		}
		catch (MalformedURLException e)
		{
			return parsePathSource(sourceString);
		}
	}

	private ContentQuery parsePathSource(String sourceString)
	{
		FileContentSource contentSource = new FileContentSource(new File(sourceString));
		return new ContentSourceQuery(contentSource, new ContentRequest(""));
	}

	private void installDefaultBuilders(final Configuration configuration)
	{
		// TODO: make this data-driven.
		configuration.installBuilder(new Builder<MongoPool>() {
			@Override
			public Class<MongoPool> getClientClass() { return MongoPool.class; }

			@Override
			public MongoPool build(DQuery dq) { return mongoPool; }
		});
		configuration.installBuilder(new ContentSourceBuilder(configuration));
		configuration.installBuilder(new ContentDrainBuilder(configuration));
	}
}
