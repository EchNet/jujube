package net.ech.config;

public abstract class IBeanDescriptor
{
	public static SubtypeDescriptor[] getSubtypeDescriptors()
	{
		return new SubtypeDescriptor[] {
			new SubtypeDescriptor(Bean.class, new ConfigPattern[] {
				new PropertyDefinedConfigPattern("property"),
			}),
			new SubtypeDescriptor(Bean.class, new ConfigPattern[] {
				new PropertyDefinedConfigPattern("properly"),
			}),
		};
	}
}
