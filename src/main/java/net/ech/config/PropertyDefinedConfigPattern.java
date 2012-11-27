package net.ech.config;

import net.ech.util.DQuery;
import net.ech.util.DPath;

/**
 * Support for the Subtypeable pattern.  
 */
public class PropertyDefinedConfigPattern implements ConfigPattern
{
	private String propertyName;

	public PropertyDefinedConfigPattern(String propertyName)
	{
		this.propertyName = propertyName;
	}

	public boolean matches(DQuery configuration)
	{
		DQuery subConfig = configuration.find(new DPath(propertyName));
		return !subConfig.isNull();
	}
}
