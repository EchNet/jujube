package net.ech.config;

import net.ech.util.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

abstract public class AbstractBuilder<T> 
	implements Builder<T>
{
	private Configuration configuration;
	private Set<String> activeKeys = new HashSet<String>();

    public AbstractBuilder(Configuration configuration)
	{
		this.configuration = configuration;
	}

	public Configuration getConfiguration()
	{
		return configuration;
	}

	@Override
    public T build(DQuery dq)
        throws IOException
	{
		if (dq.isNull()) {
			return buildFromNull(dq);
		}
		if (dq.get(String.class) != null) {
			String key = dq.get(String.class);
			if (activeKeys.contains(key)) {
				throw new DocumentException("cycle detected: " + activeKeys);
			}
			activeKeys.add(key);
			try {
				return configuration.getBean(key, this);
			}
			finally {
				activeKeys.remove(key);
			}
		}
		if (dq.get(List.class) != null) {
			return buildAggregate(dq);
		}

		String builderName = dq.find("_builder").cast(String.class, null);
		if (builderName != null && !builderName.equals(this.getClass().getName())) {
			return buildByBuilder(dq, builderName);
		}

		return buildByType(dq, dq.find("_type").cast(String.class, null));
	}

	protected T buildFromNull(DQuery dq)
		throws IOException
	{
		return throwError(dq, "configuration required");
	}

	protected T buildByType(DQuery dq, String type)
		throws IOException
	{
		if (type == null) {
			return throwError(dq, "invalid configuration");
		}
		else {
			return throwError(dq, type + ": bad configuration type");
		}
	}

	protected T buildAggregate(DQuery dq)
		throws IOException
	{
		return throwError(dq, "aggregate configuration not supported");
	}

	private T buildByBuilder(DQuery dq, String builderName)
		throws IOException
	{
		try
		{
			return ((Builder<T>) Class.forName(builderName).getConstructor(Configuration.class).newInstance(configuration)).build(dq);
		}
		catch (NoSuchMethodException e)
		{
			return throwError(dq, "bad builder");
		}
		catch (IllegalAccessException e)
		{
			return throwError(dq, "bad builder");
		}
		catch (InstantiationException e)
		{
			return throwError(dq, "bad builder");
		}
		catch (ClassCastException e)
		{
			return throwError(dq, "bad builder");
		}
		catch (ClassNotFoundException e)
		{
			return throwError(dq, "bad builder");
		}
		catch (InvocationTargetException e)
		{
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			}
			return throwError(dq, "bad builder");
		}
	}

	private T throwError(DQuery dq, String msg)
		throws DocumentException
	{
		throw new DocumentException(dq.getPath() + ": " + msg + " for " + getClientClass().getName());
	}
}
