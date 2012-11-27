package net.ech.config;

import java.lang.reflect.*;
import net.ech.util.DPredicate;

/**
 * Support for the Subtypeable pattern.  
 */
public class SubtypeDescriptor
{
	private Class<?> subtype;
	private DPredicate configPredicate;

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

	public SubtypeDescriptor(Class<?> subtype, DPredicate configPredicate)
	{
		this.subtype = subtype;
		this.configPredicate = configPredicate;
	}

	public Class<?> getSubtype()
	{
		return subtype;
	}

	public DPredicate getConfigPredicate()
	{
		return configPredicate;
	}
}
