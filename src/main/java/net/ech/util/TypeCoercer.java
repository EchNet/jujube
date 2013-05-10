package net.ech.util;

interface TypeCoercer
{
	/**
	 * Coerce a value to a type, for assignment
	 * @param rhsValue  the value to coerce
	 * @return the value, coerced to the type of this coercer, or null if no coercion is possible.
	 */
	public Object coerce(Object rhsValue);
}
