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
		TypeDescriptor typeDescriptor = TypeDescriptor.discover(type);
		return typeDescriptor == null ? null : typeDescriptor.getSubtypeDescriptors();
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
