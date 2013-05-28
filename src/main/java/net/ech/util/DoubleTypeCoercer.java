package net.ech.util;

class DoubleTypeCoercer
	implements TypeCoercer
{
	/**
	 * Coerce a non-float value to floating point type.
	 * @param rhsValue  the value to coerce
	 * @return the value as a Double, or null if no coercion is possible.
	 */
	@Override
	public Object coerce(Object rhsValue)
	{
		// Permit assignment of number to double
		if (rhsValue instanceof Number) {
			Number num = (Number) rhsValue;
			return new Double(num.doubleValue());
		}
		return null;
	}
}
