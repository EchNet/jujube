package net.ech.util;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Access a JavaBean as a Map.
 */
public class BeanPropertyMap
	extends AbstractMap<String,Object>
	implements Map<String,Object>
{
	private Object bean;
	private Map<String,PropertyDescriptor> propDescs;

	/**
	 * Constructor.
	 * @param bean   the bean that backs the map
	 */
	public BeanPropertyMap(Object bean)
		throws BeanException
	{
		this.bean = bean;
		this.propDescs = new HashMap<String,PropertyDescriptor>();

		try {
			BeanInfo bInfo = Introspector.getBeanInfo(getBeanClass(), Object.class);
			for (PropertyDescriptor pd : bInfo.getPropertyDescriptors()) {
				propDescs.put(pd.getName(), pd);
			}
		}
		catch (IntrospectionException e) {
			// Tried really hard to make it happen.  Maybe it doesn't.
		}
	}

	public Class<?> getBeanClass()
	{
		return bean.getClass();
	}

	@Override
	public Set<Map.Entry<String,Object>> entrySet()
	{
		return new AbstractSet<Map.Entry<String,Object>>()
		{
			public int size() {
				return propDescs.size();
			}

			public Iterator<Map.Entry<String,Object>> iterator() {

				return new Iterator<Map.Entry<String,Object>>()
				{
					Iterator<Map.Entry<String,PropertyDescriptor>> inner = propDescs.entrySet().iterator();

					public boolean hasNext() {
						return inner.hasNext();
					}

					public Map.Entry<String,Object> next() {

						final PropertyDescriptor pDesc = inner.next().getValue();

						return new Map.Entry<String,Object>() {

							public String getKey() {
								return pDesc.getName();
							}

							public Object getValue() {
								return getPropertyValue(pDesc, true);
							}

							public Object setValue(Object value) {
								return setPropertyValue(pDesc, value);
							}
						};
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	@Override
	public Object put(String key, Object value)
	{
		if (!propDescs.containsKey(key)) {
			throw new BeanException(getBeanClass(), getBeanClass().getName() + "." + key + ": no such property");
		}
		return setPropertyValue(propDescs.get(key), value);
	}

	/**
	 * Set a bean property via reflection.
	 */
	private Object setPropertyValue(PropertyDescriptor pDesc, Object value)
		throws BeanException
	{
		Method writeMethod = pDesc.getWriteMethod();
		if (writeMethod == null) {
			throw new BeanException(getBeanClass(), getBeanClass().getName() + "." + pDesc.getName() + ": no setter");
		}
		Object oldValue = getPropertyValue(pDesc, false);
		Exception error = null;
		try {
			writeMethod.invoke(bean, new Object[] { coerce(value, pDesc.getPropertyType()) });
			return oldValue;
		}
		catch (Exception e) {
			error = e;
		}
		throw new BeanException(getBeanClass(), writeMethod.toString(), error);
	}

	/**
	 * Get a bean property via reflection.
	 */
	private Object getPropertyValue(PropertyDescriptor pDesc, boolean readRequired)
		throws BeanException
	{
		Method readMethod = pDesc.getReadMethod();
		if (readMethod == null) {
			if (readRequired) {
				throw new BeanException(getBeanClass(), getBeanClass().getName() + "." + pDesc.getName() + ": no getter");
			}
			return null;
		}

		Exception error = null;
		try {
			return readMethod.invoke(bean, null);
		}
		catch (Exception e) {
			error = e;
		}
		throw new BeanException(getBeanClass(), pDesc.getReadMethod().toString(), error);
	}

	/**
	 * Built-in type conversions, to work within the limitations of the JSON parser.
	 */
	private Object coerce(Object obj, Class<?> expectedClass)
	{
		if (obj != null && !expectedClass.isAssignableFrom(obj.getClass())) {
			// Permit assignment of single character string to char.
			if (obj instanceof String) {
				String str = (String) obj;
				if ((expectedClass.equals(Character.class) ||
					 expectedClass.equals(char.class)) && str.length() == 1) {
					return new Character(str.charAt(0));
				}
			}

			// Permit assignment of array to List.
			if (expectedClass.isAssignableFrom(List.class) && obj.getClass().isArray()) {
				return Arrays.asList((Object[]) obj);
			}

			// Permit assignment of List to array.
			if (expectedClass.isArray() && (obj instanceof List)) {
				return ((List) obj).toArray((Object[]) Array.newInstance(expectedClass.getComponentType(), 0));
			}
		}

		return obj;
	}
}
