package net.ech.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

class BeanPropertyMapSupport
{
	private static Map<Class<?>,PropertyDescriptorMap> cache = Collections.synchronizedMap(new HashMap<Class<?>,PropertyDescriptorMap>());

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

	// For readability, plain and simple.
	private static class PropertyDescriptorMap
		extends HashMap<String,PropertyDescriptor> 
	{

		/**
		 * 
		 */
		private static final long	serialVersionUID	= -8579774545118932767L;
	}
}
