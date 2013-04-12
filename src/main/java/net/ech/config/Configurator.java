package net.ech.config;

import java.io.IOException;

/**
 * A type of thing that can produce a configured object.
 */
public interface Configurator
{
	public <T> T configure(Class<T> requiredClass)
		throws IOException;
}
