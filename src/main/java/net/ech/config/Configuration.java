package net.ech.config;

import net.ech.io.*;
import net.ech.util.*;
import java.io.*;
import java.util.*;

//
// Class Configuration is responsible for parsing a configuration file, instantiating the objects
// specified in the document, and watching the source file for modifications.  An update to the
// configuration source is reflected afterward (possibly after a short delay) in its Configuration.
//
public class Configuration
{
	private ContentQuery source;
	private ContentHandle content;
	private String version;
    private Map<Class<?>,Builder<?>> builders = new HashMap<Class<?>,Builder<?>>();
    private Map<String,ConfigMapping> namedBeans;

    /**
     * Constructor.
     */
    public Configuration(ContentQuery source)
    {
        this.source = source;
    }

	public void installBuilder(Builder<?> builder)
	{
		builders.put(builder.getClientClass(), builder);
	}

	/**
	 * Get the underlying content.
	 */
	public ContentHandle getContent()
		throws IOException
	{
		refresh();
		return content;
	}

	/**
	 * Get the version of the configuration document.
	 */
	public String getVersion()
		throws IOException
	{
		refresh();
		return version;
	}

	/**
	 * Get a raw string value.
	 */
	public String getString(String key, String defaultValue)
		throws IOException
	{
        refresh();
		return new DQuery(content.getDocument()).find(key).cast(String.class, defaultValue);
	}

	/**
	 * Get a configuration bean.  Its class alone is enough to identify it.
	 */
    public <T> T getBean(Class<T> beanClass)
        throws IOException
    {
		return getBean(beanClass.getName(), beanClass);
	}

	/**
	 * Get a configuration bean.  Its name identifies it; if it is not of the given type,
	 * an error results.
	 */
    public <T> T getBean(String key, Class<T> beanClass)
        throws IOException
    {
		return getBean(key, beanClass, null);
	}

    public <T> T getBean(String key, Builder<T> builder)
        throws IOException
    {
		return getBean(key, builder.getClientClass(), builder);
	}

	private <T> T getBean(String key, Class<T> beanClass, Builder<T> builder)
		throws IOException
	{
        // refresh() loads or reloads the document.
        refresh();

		// If the key does not appear in the configuration, create a detached entry for it.
		// This method succeeds only if the Builder can build from null.
		ConfigMapping mapping = namedBeans.get(key);
		if (mapping == null) {
			mapping = new ConfigMapping(new DQuery(content.getDocument()).find(key));
		}

		mapping.buildBean(builder, beanClass);

		try {
			return beanClass.cast(mapping.bean);
		}
		catch (ClassCastException e) {
			throw new DocumentException(key + ": expected " + beanClass.getName() + ", got " + mapping.bean);
		}
    }

	private Builder<?> getBuilder(DQuery source, Class<?> defaultBeanClass)
		throws IOException
	{
		Class<?> beanClass = getBeanClass(source, defaultBeanClass);
		Builder<?> builder = builders.get(beanClass);
		if (builder == null) {
			throw new IOException("Don't know how to build " + beanClass.getName());
		}
		return builder;
	}

	private Class<?> getBeanClass(DQuery source, Class<?> defaultBeanClass)
		throws DocumentException
	{
		String className = source.find("_class").get(String.class);
		if (className != null) {
			try {
				return Class.forName(className);
			}
			catch (Exception e) {
				throw new DocumentException(source.find("_class").getPath() + ": no such class " + className);
			}
		}
		return defaultBeanClass;
	}

    private void refresh()
        throws IOException
    {
		ContentHandle newContentHandle = source.query();
		String newVersion = newContentHandle.getVersion();
		if (outOfDate(newVersion)) {
			synchronized (this) {
				if (outOfDate(newVersion)) {
					this.content = newContentHandle;
					this.version = newVersion;
					this.namedBeans = buildBeanCache(new DQuery(newContentHandle.getDocument()));
				}
			}
		}
    }

	private boolean outOfDate(String newVersion)
	{
		return this.namedBeans == null || !newVersion.equals(this.version);
	}

	private Map<String,ConfigMapping> buildBeanCache(DQuery document)
		throws IOException
	{
		final Map<String,ConfigMapping> namedBeans = new HashMap<String,ConfigMapping>();

		// Default mappings for items identified by hash.
		for (Class<?> builderClass : builders.keySet()) {
			namedBeans.put(builderClass.getName(), new ConfigMapping(new DQuery(null)));
		}

		// Mappings for each child of the document root.
		document.each(new DHandler() {
			public void handle(DQuery child) throws DocumentException {
				namedBeans.put(child.getPath().getLast().toString(), new ConfigMapping(child));
			}
		});

		return namedBeans;
	}

	private class ConfigMapping
	{
		DQuery source;
		Object bean;

		ConfigMapping(DQuery source) {
			this.source = source;
		}

		// First thread who asks for this bean, and only the first thread, gets to build it.
		public void buildBean(Builder<?> builder, Class<?> beanClass) 
			throws IOException
		{
			if (bean == null) {
				synchronized (this) {
					if (bean == null) {
						bean = (builder == null ? getBuilder(source, beanClass) : builder).build(source);
					}
				}
			}
		}
	}
}
