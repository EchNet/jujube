package net.ech.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
		try {
			this.propDescs = BeanPropertyMapSupport.getPropertyDescriptorMap(getBeanClass());
		}
		catch (final java.beans.IntrospectionException e) {
			// Tried hard to make it happen in unit tests.  Maybe it doesn't.
			throw new BeanException(getBeanClass(), e);
		}
	}

	public Class<?> getBeanClass()
	{
		return bean.getClass();
	}

	public Class<?> getPropertyClass(String key)
	{
		return propDescs.containsKey(key) ?  propDescs.get(key).getPropertyType() : null;
	}

	public boolean hasProperty(String key)
	{
		return propDescs.containsKey(key);
	}

	public void assertProperty(String key)
		throws BeanException
	{
		if (!hasProperty(key)) {
			throw new BeanException(getBeanClass(), getBeanClass().getName() + "." + key + ": no set method");
		}
	}

	@Override
	public Set<Map.Entry<String,Object>> entrySet()
	{
		return new AbstractSet<Map.Entry<String,Object>>()
		{
			@Override
			public int size() {
				return propDescs.size();
			}

			@Override
			public Iterator<Map.Entry<String,Object>> iterator() {

				return new Iterator<Map.Entry<String,Object>>()
				{
					Iterator<Map.Entry<String,PropertyDescriptor>> inner = propDescs.entrySet().iterator();

					@Override
					public boolean hasNext() {
						return inner.hasNext();
					}

					@Override
					public Map.Entry<String,Object> next() {

						final PropertyDescriptor pDesc = inner.next().getValue();

						return new Map.Entry<String,Object>() {

							@Override
							public String getKey() {
								return pDesc.getName();
							}

							@Override
							public Object getValue() {
								return getPropertyValue(pDesc, true);
							}

							@Override
							public Object setValue(Object value) {
								return setPropertyValue(pDesc, value);
							}
						};
					}

					@Override
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
		assertProperty(key);
		return setPropertyValue(propDescs.get(key), value);
	}

	/**
	 * Set a bean property via reflection.
	 */
	private Object setPropertyValue(PropertyDescriptor pDesc, Object value)
		throws BeanException
	{
		final Method writeMethod = pDesc.getWriteMethod();
		if (writeMethod == null) {
			throw new BeanException(getBeanClass(), getBeanClass().getName() + "." + pDesc.getName() + ": no setter");
		}
		final Object oldValue = getPropertyValue(pDesc, false);
		Exception error = null;
		try {
			writeMethod.invoke(bean, new Object[] { TypeCoercionSupport.coerce(pDesc.getPropertyType(), value) });
			return oldValue;
		}
		catch (final Exception e) {
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
		final Method readMethod = pDesc.getReadMethod();
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
		catch (final Exception e) {
			error = e;
		}
		throw new BeanException(getBeanClass(), pDesc.getReadMethod().toString(), error);
	}
}
