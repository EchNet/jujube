package net.ech.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
			return new HashSet(Arrays.asList((Object[]) rhsValue));
		}
		if (rhsValue instanceof Collection) {
			return new HashSet((Collection) rhsValue);
		}
		return null;
	}
}
