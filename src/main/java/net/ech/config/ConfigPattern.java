package net.ech.config;

import net.ech.util.DQuery;

/**
 * Support for the Subtypeable pattern.  
 */
public interface ConfigPattern
{
	public boolean matches(DQuery configuration);
}
