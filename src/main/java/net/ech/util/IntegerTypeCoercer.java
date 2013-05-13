package net.ech.util;

class IntegerTypeCoercer
	implements TypeCoercer
{
	/**
	 * Coerce a non-integer value to integer type.
	 * @param rhsValue  the value to coerce
	 * @return the value as a Character, or null if no coercion is possible.
	 */
	@Override
	public Object coerce(Object rhsValue)
	{
		// Permit assignment of number to int, provided that there is no loss of precision
		if (rhsValue instanceof Number) {
			Number num = (Number) rhsValue;
			long longValue = num.longValue();
			double doubleValue = num.doubleValue();
			if (doubleValue == longValue && longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
				return new Integer((int) longValue);
			}
		}
		// Permit assignment of string to int.
		else if (rhsValue instanceof String) {
			try {
				return Integer.parseInt(rhsValue.toString());
			}
			catch (NumberFormatException e) {
			}
		}

		return null;
	}
}
