package net.ech.config;

import java.lang.reflect.*;

/**
 * Support for the Configurable pattern.  There are two participating classes in the Configurable pattern:
 * the target class and the configurator class.  There is a 1-1 relationship between the two.  The target
 * class has one constructor, which accepts a single parameter of the configurator class type.  The
 * Configurator class is a POJO and is a static member of the target class.
 *
 * Summary:
 *
 * public class TargetClass {
 *     public static class ConfiguratorClass {
 *         public void setProperty(String property) {...}
 *     }
 *     public TargetClass(ConfiguratorClass config) {...}
 * }
 *
 * Motivation: We wish for simple JSON-based dependency-injection-style configuration of classes supporting
 * a "configure once, use many" convention.  For instance, consider a made-up "ProducerProcess" class.  This
 * class supports various configuration settings, but once the ProducerProcess instance starts running, these
 * settings may not be adjusted.  We wish for this invariant to be reflected in the ProducerProcess API. 
 * We also wish for the ProducerProcess class to be configurable like so (properties are made up):
 * { "$class": "ProducerProcess", "limit": 1200, "waitTime": 3600 }
 *
 * Benefit: better handling of defaults for configuration values.
 *
 * Issue: how to handle non-optional configuraiton values. 
 */
public class ConfigurationDescriptor
{
	private Class<?> targetClass;
	private Class<?> configClass;
	private Constructor<?> constructor;

	/**
	 * If the given target class follows the Configurable pattern, return a descriptor
	 * that identifies the Configurator class and the participating constructor.
	 */
	public static ConfigurationDescriptor analyze(Class<?> targetClass)
	{
		Constructor[] constructors = targetClass.getConstructors();
		if (constructors.length == 1 && constructors[0].getParameterTypes().length == 1) {
			Class<?> paramClass = constructors[0].getParameterTypes()[0];
			if (!paramClass.isInterface() &&
				!paramClass.isEnum() &&
				targetClass.equals(paramClass.getDeclaringClass()))
			{
				return new ConfigurationDescriptor(targetClass, paramClass, constructors[0]);
			}
		}

		return null;
	}

	public ConfigurationDescriptor(Class<?> targetClass, Class<?> configClass, Constructor<?> constructor)
	{
		this.targetClass = targetClass;
		this.configClass = configClass;
		this.constructor = constructor;
	}

	public Class<?> getTargetClass()
	{
		return targetClass;
	}

	public Class<?> getConfiguratorClass()
	{
		return configClass;
	}

	public Constructor<?> getConstructor()
	{
		return constructor;
	}
}
