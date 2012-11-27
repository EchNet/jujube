package net.ech.config;

import java.lang.reflect.*;

/**
 * Support for the Subtypeable pattern.  
 */
public class SubtypeDescriptor
{
	private Class<?> subtype;
	private ConfigPattern[] configPatterns;

	public static SubtypeDescriptor[] discover(Class<?> type)
	{
		try {
			Class<?> descriptorClass = Class.forName(type.getName() + "Descriptor");
			Method method = descriptorClass.getMethod("getSubtypeDescriptors");
			if (Modifier.isStatic(method.getModifiers())) {
				return (SubtypeDescriptor[]) method.invoke(null);
			}
		}
		catch (ClassNotFoundException e) {
		}
		catch (NoSuchMethodException e) {
		}
		catch (SecurityException e) {
		}
		catch (IllegalAccessException e) {
		}
		catch (InvocationTargetException e) {
			// should not be reached
		}
		return null;
	}

	public SubtypeDescriptor(Class<?> subtype, ConfigPattern[] configPatterns)
	{
		this.subtype = subtype;
		this.configPatterns = configPatterns;
	}

	public Class<?> getSubtype()
	{
		return subtype;
	}

	public ConfigPattern[] getConfigPatterns()
	{
		return configPatterns;
	}
}
