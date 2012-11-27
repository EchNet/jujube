package net.ech.config;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.TypeVariable;
import java.util.*;
import net.ech.nio.json.JsonCodec;
import net.ech.util.*;

public class Whence
{
	private DQuery document;
	private Map<DPath,Object> cache;
	private Map<Class<?>,List<SubtypeDescriptor>> subtypeDescriptorMap;

	public Whence(Object document)
	{
		this.document = new DQuery(document);
		this.cache = new HashMap<DPath,Object>();
		this.subtypeDescriptorMap = new HashMap<Class<?>,List<SubtypeDescriptor>>();
	}

	public Object pull(String key)
		throws IOException
	{
		return pull(key, Object.class);
	}

	public <T> T pull(String key, Class<T> requiredClass)
		throws IOException
	{
		return requiredClass.cast(snapReference(document.find(key), requiredClass));
	}

	private Object snapReference(DQuery dq, Class<?> requiredClass)
		throws IOException
	{
		try {
			if (dq.isNull()) {
				throw new DocumentException("no such key");
			}

			String ref = dq.get(String.class);
			if (ref != null && ref.startsWith("{{") && ref.endsWith("}}")) {
				ref = ref.substring(0, ref.length() - 2).substring(2).trim();
				// TODO: cycle prevention
				return snapReference(document.find(ref), requiredClass);
			}

			if (dq.get(Map.class) == null && dq.get(List.class) == null) {
				return dq.get();
			}

			if (!cache.containsKey(dq.getPath())) {
				cache.put(
					dq.getPath(),
					dq.get(Map.class) != null ? materializeMap(dq, requiredClass) : materializeList(dq, requiredClass)
				);
			}
			return cache.get(dq.getPath());
		}
		catch (Exception e) {
			throw new IOException("cannot configure " + dq.getPath() + ": " + e.getMessage(), e);
		}
	}

	private Object materializeMap(DQuery dq, Class<?> requiredClass)
		throws Exception
	{
		Class<?> implClass = findImplementationClass(dq, requiredClass);

		if (implClass == null) {
			Map<String,Object> map = new HashMap<String,Object>();
			mapProperties(dq, null, map);
			return map;
		}

		ConfigurationDescriptor configDesc = ConfigurationDescriptor.analyze(implClass);
		Object obj = (configDesc == null ? implClass : configDesc.getConfiguratorClass()).newInstance();
		BeanPropertyMap bpm = new BeanPropertyMap(obj);
		mapProperties(dq, bpm, bpm);
		return configDesc == null ? obj : configDesc.getConstructor().newInstance(obj);
	}

	private Object materializeList(DQuery dq, Class<?> requiredClass)
		throws IOException
	{
		if (requiredClass != null && requiredClass.isArray()) {
			Object result =  Array.newInstance(requiredClass.getComponentType(), ((List) dq.get()).size());
			arrayProperties(dq, requiredClass.getComponentType(), result);
			return result;
		}
		else {
			List<Object> result = new ArrayList<Object>();
			listProperties(dq, result);
			return result;
		}
	}

	private Class<?> findImplementationClass(DQuery dq, Class<?> requiredClass)
		throws Exception
	{
		Class<?> implClass = dq.find("$class").isNull() ? requiredClass : Class.forName(dq.find("$class").require(String.class));

		// Catch type mismatches.
		if (requiredClass != null && !requiredClass.equals(implClass)) {
			implClass.asSubclass(requiredClass);  // throws ClassCastException
		}

		// If Object or Map is requested explicitly, return null to indicate that a Map should be instantiated.
		if (Map.class.equals(implClass) || Object.class.equals(implClass)) {
			implClass = null;
		}
		if (implClass != null && implClass.isInterface()) {
			// Handle request for interface by looking up a matching subtype configuration descriptor.
			boolean foundSubtype = false;
		nextSubtype:
			for (SubtypeDescriptor subtypeDescriptor : getSubtypeDescriptors(requiredClass)) {
				for (ConfigPattern configPattern : subtypeDescriptor.getConfigPatterns()) {
					if (!configPattern.matches(dq)) {
						continue nextSubtype;
					}
				}
				if (foundSubtype) {
					throw new DocumentException(dq.getPath() + ": ambiguous subtype");
				}
				implClass = subtypeDescriptor.getSubtype();
				foundSubtype = true;
			}
			if (!foundSubtype) {
				throw new DocumentException(dq.getPath() + ": does not appear to configure a subtype of " + requiredClass);
			}
		}

		return implClass;
	}

	private void mapProperties(final DQuery dq, final BeanPropertyMap bpm, final Map<String,Object> map)
		throws IOException
	{
		dq.each(new DHandler() {
			public void handle(DQuery cdq) throws IOException {
				String key = cdq.getPath().getLast().toString();
				if (!key.startsWith("$")) {
					map.put(key, snapReference(cdq, bpm == null ? null : bpm.getPropertyClass(key)));
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
				Array.set(result, index++, snapReference(cdq, implElementClass));
			}
		});
	}

	private void listProperties(final DQuery dq, final List<Object> result)
		throws IOException
	{
		dq.each(new DHandler() {
			public void handle(DQuery cdq) throws IOException {
				result.add(snapReference(cdq, null));
			}
		});
	}

	private List<SubtypeDescriptor> getSubtypeDescriptors(Class<?> iClass)
	{
		if (!subtypeDescriptorMap.containsKey(iClass)) {

			SubtypeDescriptor[] subtypeDescriptors = SubtypeDescriptor.discover(iClass);
			if (subtypeDescriptors == null) {
				throw new IllegalArgumentException(iClass.getName() + " is an interface having no subtype descriptors");
			}

			subtypeDescriptorMap.put(iClass, Arrays.asList(subtypeDescriptors));
		}
		return subtypeDescriptorMap.get(iClass);
	}
}
