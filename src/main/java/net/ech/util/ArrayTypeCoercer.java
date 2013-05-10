package net.ech.util;

import java.lang.reflect.Array;
import java.util.Collection;

class ArrayTypeCoercer
	implements TypeCoercer
{
	private Class<?> componentType;

	public ArrayTypeCoercer(Class<?> componentType)
	{
		this.componentType = componentType;
	}

	/**
	 * Coerce a non-array value to array type.
	 * @param rhsValue  the value to coerce
	 * @return the value as an array, or null if no coercion is possible
	 */
	@Override
	public Object coerce(Object rhsValue)
		throws TypeMismatchException
	{
		// Permit assignment of Collection to array.
		if (rhsValue instanceof Collection) {
			Collection<?> list = (Collection<?>) rhsValue;
			Object array = Array.newInstance(componentType, list.size());
			int index = 0;
			for (Object element : list) {
				try {
					Array.set(array, index++, element);
				}
				catch (IllegalArgumentException e) {
					throw new TypeMismatchException(componentType, element, e);
				}
			}
			return array;
		}
		return null;
	}
}
