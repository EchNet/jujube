package net.ech.config;

import java.io.IOException;

/**
 * A Configurator is bound to some sort of configuration data and builds the object described
 * by the configuration.
 */
public interface Configurator
{
	/**
	 * Produce the object described by my configuration.
	 * @param requiredClass   Type to cast the object to
	 * @throws IOException   In case of an error retrieving or executing the configuration
	 * @throws ClassCastException   If the object is not of the required type
	 */
	public <T> T configure(Class<T> requiredClass)
		throws IOException;
}
