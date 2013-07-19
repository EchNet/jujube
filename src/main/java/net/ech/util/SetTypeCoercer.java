package net.ech.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

class SetTypeCoercer
	implements TypeCoercer
{
	/**
	 * Coerce a non-Set value to Set type.
	 * @param rhsValue  the value to coerce
	 * @return the value as a Set, or null if no coercion is possible.
	 */
	@Override
	public Object coerce(Object rhsValue)
	{
		// Permit assignment of array to Set.
		if (rhsValue.getClass().isArray()) {
			return new HashSet<Object>(Arrays.asList((Object[]) rhsValue));
		}
		if (rhsValue instanceof Collection) {
			return new HashSet<Object>((Collection<?>) rhsValue);
		}
		return null;
	}
}
