package net.ech.config;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import net.ech.doc.Document;
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
		throws ConfigException
	{
		return new Builder().build(requiredClass);
	}
		
	private class Builder
	{
		private Map<String,Object> context = new HashMap<String,Object>();
		private List<Map<String,Object>> contextHistory = new ArrayList<Map<String,Object>>();

		public <T> T build(Class<T> requiredClass)
			throws ConfigException
		{
			context.put("$document", document);
			context.put("$resolver", documentResolver);
			return requiredClass.cast(snapReference(document, requiredClass));
		}

		private Object snapReference(Document dq, Class<?> requiredClass)
			throws ConfigException
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
			catch (ConfigException e) {
				throw e;
			}
			catch (IOException e) {
				throw new ConfigException("cannot configure " + dq.getPath() + ": " + e.getMessage(), e);
			}
		}

		private Document fillDocument(Document dq)
			throws IOException
		{
			Document extendsDoc = dq.find("__extends");
			if (!extendsDoc.isNull()) {
				String superName = extendsDoc.get(String.class);
				return extendDocument(superName != null ? Collections.singletonList(superName) : extendsDoc.require(List.class), dq);
			}
			return dq;
		}

		private Document extendDocument(List<String> superList, Document dq)
			throws IOException
		{
			Document baseDoc = null;
			for (String key : superList) {
				Document superDoc = documentResolver.resolve(key).produce();
				if (superDoc.isNull()) {
					throw new InternalConfigException(key + ": (__extends) no such key");
				}
				superDoc = fillDocument(superDoc);
				baseDoc = baseDoc == null ? superDoc : baseDoc.extend(superDoc);
			}
			return baseDoc == null ? dq : dq.extend(baseDoc);
		}

		private Object materializeMap(Document dq, Class<?> requiredClass)
			throws IOException
		{
			Object refObject = handleRef(dq);
			if (refObject != null) {
				return refObject;
			}

			Class<?> implClass = findImplementationClass(dq, requiredClass);
			boolean pushedContext = updateContext(dq);

			try {
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
			catch (InstantiationException e) {
				throw new InternalConfigException(e);
			}
			catch (IllegalAccessException e) {
				throw new InternalConfigException(e);
			}
			catch (java.lang.reflect.InvocationTargetException e) {
				throw new InternalConfigException(e);
			}
			finally {
				if (pushedContext) {
					popContext();
				}
			}
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

			throw new InternalConfigException(requiredClass + " cannot be configured with an array");
		}

		private Class<?> findImplementationClass(Document dq, Class<?> requiredClass)
			throws IOException
		{
			Class<?> implClass;
			try {
				implClass = dq.find("__type").isNull() ? requiredClass : Class.forName(dq.find("__type").require(String.class));
			}
			catch (ClassNotFoundException e) {
				throw new InternalConfigException(e);
			}

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
			throws IOException
		{
			Class<?> implClass = null;

			List<SubtypeDescriptor> subtypeDescriptors = getSubtypeDescriptors(baseClass);
			if (subtypeDescriptors != null) {
				for (SubtypeDescriptor subtypeDescriptor : subtypeDescriptors) {
					if (subtypeDescriptor.getConfigPredicate().evaluate(dq)) {
						if (implClass != null) {
							throw new InternalConfigException("ambiguous subtype");
						}
						implClass = subtypeDescriptor.getSubtype();
					}
				}
			}

			if (implClass == null) {
				throw new InternalConfigException("does not appear to configure a subtype of " + baseClass);
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

		private Object handleRef(Document dq)
			throws IOException
		{
			String ref = dq.find("__ref").cast(String.class, null);
			if (ref != null) {
				if (!context.containsKey(ref)) {
					throw new IOException(ref + ": unresolved reference");
				}
				return context.get(ref);
			}
			return null;
		}

		private boolean updateContext(Document dq)
			throws IOException
		{
			Document contextDoc = dq.find("__context");
			if (contextDoc.cast(Map.class, null) != null) {
				Map<String,Object> newContext = new HashMap<String,Object>(context);
				mapProperties(contextDoc, null, newContext);
				contextHistory.add(context);
				context = newContext;
				return true;
			}
			return false;
		}

		private void popContext()
		{
			int size = contextHistory.size();
			context = contextHistory.get(size - 1);
			contextHistory.remove(size - 1);
		}
	}

	private static List<SubtypeDescriptor> getSubtypeDescriptors(Class<?> iClass)
	{
		synchronized (subtypeDescriptorMap) {
			if (!subtypeDescriptorMap.containsKey(iClass)) {

				SubtypeDescriptor[] subtypeDescriptors = SubtypeDescriptor.discover(iClass);
				if (subtypeDescriptors == null) {
					return null;
				}

				subtypeDescriptorMap.put(iClass, Arrays.asList(subtypeDescriptors));
			}
			return subtypeDescriptorMap.get(iClass);
		}
	}

	private static class InternalConfigException
		extends IOException
	{
		InternalConfigException(String msg) {
			super(msg);
		}

		InternalConfigException(Exception e) {
			super(e);
		}
	}
}
