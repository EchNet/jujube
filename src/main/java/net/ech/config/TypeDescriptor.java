package net.ech.config;

/**
 * Support for the Subtypeable pattern.  
 */
public abstract class TypeDescriptor
{
	public static TypeDescriptor discover(Class<?> type)
	{
		try {
			return Class.forName(type.getName() + "Descriptor").asSubclass(TypeDescriptor.class).newInstance();
		}
		catch (ClassNotFoundException e) {
		}
		catch (ClassCastException e) {
		}
		catch (InstantiationException e) {
		}
		catch (SecurityException e) {
		}
		catch (IllegalAccessException e) {
		}
		return null;
	}

	public abstract Class<?> getType();

	public abstract SubtypeDescriptor[] getSubtypeDescriptors();
}
