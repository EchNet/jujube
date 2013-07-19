package net.ech.config;

import net.ech.doc.DocPredicate;

/**
 * Support for the Subtypeable pattern.  
 */
public class SubtypeDescriptor
{
	private Class<?> subtype;
	private DocPredicate configPredicate;

	public static SubtypeDescriptor[] discover(Class<?> type)
	{
		TypeDescriptor typeDescriptor = TypeDescriptor.discover(type);
		return typeDescriptor == null ? null : typeDescriptor.getSubtypeDescriptors();
	}

	public SubtypeDescriptor(Class<?> subtype, DocPredicate configPredicate)
	{
		this.subtype = subtype;
		this.configPredicate = configPredicate;
	}

	public Class<?> getSubtype()
	{
		return subtype;
	}

	public DocPredicate getConfigPredicate()
	{
		return configPredicate;
	}
}
