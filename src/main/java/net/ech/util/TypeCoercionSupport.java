package net.ech.util;

import java.util.List;
import java.util.Set;

public class TypeCoercionSupport
{
	private static TypeCoercerEntry[] typeCoercers = new TypeCoercerEntry[] {
		new TypeCoercerEntry(List.class, new ListTypeCoercer()),
		new TypeCoercerEntry(Set.class, new SetTypeCoercer()),
		new TypeCoercerEntry(Integer.class, new IntegerTypeCoercer()),
		new TypeCoercerEntry(int.class, new IntegerTypeCoercer()),
		new TypeCoercerEntry(Double.class, new DoubleTypeCoercer()),
		new TypeCoercerEntry(double.class, new DoubleTypeCoercer()),
		new TypeCoercerEntry(Character.class, new CharacterTypeCoercer()),
		new TypeCoercerEntry(char.class, new CharacterTypeCoercer()),
		new TypeCoercerEntry(Boolean.class, new BooleanTypeCoercer()),
		new TypeCoercerEntry(boolean.class, new BooleanTypeCoercer())
	};

	/**
	 * Built-in type conversions.
	 */
	public static Object coerce(Class<?> lhsType, Object rhsValue)
		throws TypeMismatchException
	{
		if (rhsValue != null && !lhsType.isAssignableFrom(rhsValue.getClass())) {
			if (lhsType.isArray()) {
				Object coerced = new ArrayTypeCoercer(lhsType.getComponentType()).coerce(rhsValue);
				if (coerced != null) {
					return coerced;
				}
			}
			else {
				for (TypeCoercerEntry typeCoercerEntry : typeCoercers) {
					if (lhsType.isAssignableFrom(typeCoercerEntry.type)) {
						Object coerced = typeCoercerEntry.coercer.coerce(rhsValue);
						if (coerced != null) {
							return coerced;
						}
					}
				}
			}
			throw new TypeMismatchException(lhsType, rhsValue);
		}

		return rhsValue;
	}

	private static class TypeCoercerEntry
	{
		Class<?> type;
		TypeCoercer coercer;

		TypeCoercerEntry(Class<?> type, TypeCoercer coercer)
		{
			this.type = type;
			this.coercer = coercer;
		}
	}
}
