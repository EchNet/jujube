package net.ech.util;

import java.util.Arrays;
import java.util.List;

class ListTypeCoercer
	implements TypeCoercer
{
	/**
	 * Coerce a non-List value to List type.
	 * @param rhsValue  the value to coerce
	 * @return the value as a List, or null if no coercion is possible
	 */
	@Override
	public Object coerce(Object rhsValue)
		throws TypeMismatchException
	{
		// Permit assignment of array to List.
		if (rhsValue.getClass().isArray()) {
			return Arrays.asList((Object[]) rhsValue);
		}
		return null;
	}
}
