package net.ech.config;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import net.ech.doc.Document;
import net.ech.doc.DocumentException;
import net.ech.doc.DocumentResolver;
import net.ech.doc.DocPath;
import net.ech.util.BeanPropertyMap;

public class DocumentBasedConfigurator
	implements Configurator
{
	private static Map<Class<?>,List<SubtypeDescriptor>> subtypeDescriptorMap = new HashMap<Class<?>,List<SubtypeDescriptor>>();

	private Document document;
	private DocumentResolver documentResolver;
	private Map<DocPath,Object> cache;

	public DocumentBasedConfigurator(String key, DocumentResolver documentResolver)
		throws IOException
	{
		this(documentResolver.resolve(key).produce(), documentResolver);
	}

	public DocumentBasedConfigurator(Document document, DocumentResolver documentResolver)
	{
		this.document = document;
		this.documentResolver = documentResolver;
		this.cache = new HashMap<DocPath,Object>();
	}

	@Override
	public <T> T configure(Class<T> requiredClass)
		throws IOException
	{
		return requiredClass.cast(snapReference(document, requiredClass));
	}

	private Object snapReference(Document dq, Class<?> requiredClass)
		throws IOException
	{
		try {
			dq = fillDocument(dq);

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

	private Document fillDocument(Document dq)
		throws IOException
	{
		String superDocKey = dq.find("__extends").get(String.class);
		if (superDocKey != null) {
			Document superDoc = documentResolver.resolve(superDocKey).produce();
			if (superDoc.isNull()) {
				throw new DocumentException(superDocKey + ": (__extends) no such key");
			}
			dq = dq.extend(fillDocument(superDoc));
		}
		return dq;
	}

	private Object materializeMap(Document dq, Class<?> requiredClass)
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

	private Object materializeList(Document dq, Class<?> requiredClass)
		throws IOException
	{
		if (Object.class.equals(requiredClass) || List.class.equals(requiredClass)) {
			List<Object> result = new ArrayList<Object>();
			listProperties(dq, result);
			return result;
		}

		if (requiredClass.isArray()) {
			Object result =  Array.newInstance(requiredClass.getComponentType(), ((List) dq.get()).size());
			arrayProperties(dq, requiredClass.getComponentType(), result);
			return result;
		}

		throw new IllegalArgumentException(requiredClass + " cannot be configured with an array");
	}

	private Class<?> findImplementationClass(Document dq, Class<?> requiredClass)
		throws Exception
	{
		Class<?> implClass = dq.find("__type").isNull() ? requiredClass : Class.forName(dq.find("__type").require(String.class));

		// Catch type mismatches.
		if (!requiredClass.equals(implClass)) {
			implClass.asSubclass(requiredClass);  // throws ClassCastException
		}

		if (Map.class.equals(implClass) || Object.class.equals(implClass)) {
			// If Object or Map is requested explicitly, return null to indicate that a Map should be instantiated.
			implClass = null;
		}
		else if (implClass.isInterface()) {
			// Handle request for interface by looking up a matching subtype configuration descriptor.
			implClass = findMatchingSubtype(dq, implClass);
		}

		return implClass;
	}

	private Class<?> findMatchingSubtype(Document dq, Class<?> baseClass)
		throws DocumentException
	{
		Class<?> implClass = null;

		for (SubtypeDescriptor subtypeDescriptor : getSubtypeDescriptors(baseClass)) {
			if (subtypeDescriptor.getConfigPredicate().evaluate(dq)) {
				if (implClass != null) {
					throw new DocumentException("ambiguous subtype");
				}
				implClass = subtypeDescriptor.getSubtype();
			}
		}

		if (implClass == null) {
			throw new DocumentException("does not appear to configure a subtype of " + baseClass);
		}
		return implClass;
	}

	private void mapProperties(Document dq, BeanPropertyMap bpm, Map<String,Object> map)
		throws IOException
	{
		for (Document cdq : dq.children()) {
			String key = cdq.getPath().getLast().toString();
			if (!key.startsWith("_")) {
				map.put(key, snapReference(cdq, bpm == null ? Object.class : bpm.getPropertyClass(key)));
			}
		}
	}

	private void arrayProperties(Document dq, Class<?> implElementClass, Object result)
		throws IOException
	{
		int index = 0;
		for (Document cdq : dq.children()) {
			Array.set(result, index++, snapReference(cdq, implElementClass));
		}
	}

	private void listProperties(Document dq, List<Object> result)
		throws IOException
	{
		for (Document cdq : dq.children()) {
			result.add(snapReference(cdq, Object.class));
		}
	}

	private List<SubtypeDescriptor> getSubtypeDescriptors(Class<?> iClass)
	{
		synchronized (subtypeDescriptorMap) {
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
}
