package net.ech.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

class BeanPropertyMapSupport
{
	private static Map<Class<?>,PropertyDescriptorMap> cache = Collections.synchronizedMap(new HashMap<Class<?>,PropertyDescriptorMap>());

	private static TypeCoercerEntry[] typeCoercers = new TypeCoercerEntry[] {
		new TypeCoercerEntry(List.class, new ListTypeCoercer()),
		new TypeCoercerEntry(Set.class, new SetTypeCoercer()),
		new TypeCoercerEntry(Character.class, new CharacterTypeCoercer()),
		new TypeCoercerEntry(char.class, new CharacterTypeCoercer())
	};

	public static Map<String,PropertyDescriptor> getPropertyDescriptorMap(Class<?> beanClass)
		throws java.beans.IntrospectionException
	{
		if (!cache.containsKey(beanClass)) {
			PropertyDescriptorMap map = new PropertyDescriptorMap();
			BeanInfo bInfo = Introspector.getBeanInfo(beanClass, Object.class);
			for (PropertyDescriptor pd : bInfo.getPropertyDescriptors()) {
				map.put(pd.getName(), pd);
			}
			cache.put(beanClass, map);
		}
		return cache.get(beanClass);
	}

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

	// For readability, plain and simple.
	private static class PropertyDescriptorMap
		extends HashMap<String,PropertyDescriptor> 
	{}

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
