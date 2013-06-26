package net.ech.util;

class BooleanTypeCoercer
	implements TypeCoercer
{
	/**
	 * Coerce a non-boolean value to boolean type.
	 * @param rhsValue  the value to coerce
	 * @return the value as a Boolean, or null if no coercion is possible.
	 */
	@Override
	public Object coerce(Object rhsValue)
	{
		// Enable assignment of Boolean to boolean.
		if (rhsValue instanceof Boolean) {
			return rhsValue;
		}

		// Permit assignment of strings "true" and "false" to boolean.
		if (rhsValue instanceof String) {
			String str = (String) rhsValue;
			if ("true".equals(str)) {
				return new Boolean(true);
			}
			if ("false".equals(str)) {
				return new Boolean(false);
			}
		}

		return null;
	}
}
