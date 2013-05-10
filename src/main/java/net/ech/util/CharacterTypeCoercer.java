package net.ech.util;

class CharacterTypeCoercer
	implements TypeCoercer
{
	/**
	 * Coerce a non-character value to character type.
	 * @param rhsValue  the value to coerce
	 * @return the value as a Character, or null if no coercion is possible.
	 */
	@Override
	public Object coerce(Object rhsValue)
	{
		// Permit assignment of single character string to char.
		if (rhsValue instanceof String) {
			String str = (String) rhsValue;
			if (str.length() == 1) {
				return new Character(str.charAt(0));
			}
		}

		// Permit assignment of number to char, if the number is in range.
		if (rhsValue instanceof Number) {
			int value = ((Number) rhsValue).intValue();
			if (value >= 0 && value <= Character.MAX_VALUE) {
				return new Character((char) value);
			}
		}

		return null;
	}
}
