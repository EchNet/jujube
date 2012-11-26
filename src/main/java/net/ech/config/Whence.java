package net.ech.config;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.TypeVariable;
import java.util.*;
import net.ech.nio.json.JsonCodec;
import net.ech.util.*;

public class Whence
{
	private final static Map<String,Object> A_MAP = new HashMap<String,Object>();

	private Object document;
	private Map<DPath,Object> cache;

	public Whence(Object document)
	{
		this.document = document;
		this.cache = new HashMap<DPath,Object>();
	}

	public Object pull(String key)
		throws IOException
	{
		return pull(key, Object.class);
	}

	public <T> T pull(String key, Class<T> clazz)
		throws IOException
	{
		return clazz.cast(pull(new DQuery(document).find(key), clazz));
	}

	private Object pull(DQuery dq, Class<?> implClass)
		throws IOException
	{
		try {
			if (dq.isNull()) {
				throw new DocumentException("no such key");
			}
			
			if (dq.get() instanceof Map) {

				if (!dq.find("$class").isNull()) {
					implClass = Class.forName(dq.find("$class").require(String.class));
				}

				ConfigurationDescriptor configDesc = null;
				if (implClass != null) {
					configDesc = ConfigurationDescriptor.analyze(implClass);
				}
				if (configDesc != null) {

					Object config = configDesc.getConfiguratorClass().newInstance();
					BeanPropertyMap bpm = new BeanPropertyMap(config);
					mapProperties(dq, bpm, bpm);
					return configDesc.getConstructor().newInstance(config);
				}

				Object result;
				BeanPropertyMap bpm = null;
				Map<String,Object> map;
				if (implClass != null && !implClass.isInstance(A_MAP)) {
					if (implClass.isInterface()) {
						throw new IllegalArgumentException(implClass.getName() + " is an interface");
					}
					result = implClass.newInstance();
					map = bpm = new BeanPropertyMap(result);
				}
				else {
					result = map = new HashMap<String,Object>();
				}
				mapProperties(dq, bpm, map);
				return result;
			}

			if (dq.get() instanceof List) {
				if (implClass != null && implClass.isArray()) {
					Object result =  Array.newInstance(implClass.getComponentType(), ((List) dq.get()).size());
					arrayProperties(dq, implClass.getComponentType(), result);
					return result;
				}
				else {
					List<Object> result = new ArrayList<Object>();
					listProperties(dq, result);
					return result;
				}
			}

			return dq.get();
		}
		catch (Exception e) {
			throw new IOException("cannot configure " + dq.getPath() + ": " + e.getMessage(), e);
		}
	}

	private void mapProperties(final DQuery dq, final BeanPropertyMap bpm, final Map<String,Object> map)
		throws IOException
	{
		dq.each(new DHandler() {
			public void handle(DQuery cdq) throws IOException {
				String key = cdq.getPath().getLast().toString();
				if (!key.startsWith("$")) {
					map.put(key, pull(cdq, bpm == null ? null : bpm.getPropertyClass(key)));
				}
			}
		});
	}

	private void arrayProperties(final DQuery dq, final Class<?> implElementClass, final Object result)
		throws IOException
	{
		dq.each(new DHandler() {
			int index = 0;
			public void handle(DQuery cdq) throws IOException {
				Array.set(result, index++, pull(cdq, implElementClass));
			}
		});
	}

	private void listProperties(final DQuery dq, final List<Object> result)
		throws IOException
	{
		dq.each(new DHandler() {
			public void handle(DQuery cdq) throws IOException {
				result.add(pull(cdq, null));
			}
		});
	}
}
