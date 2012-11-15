package net.ech.config;

import java.io.IOException;
import java.util.*;
import net.ech.nio.json.JsonCodec;
import net.ech.util.*;

public class Whence
{
	private final static Map<String,Object> A_MAP = new HashMap<String,Object>();

	private Object document;

	public Whence(Object document)
	{
		this.document = document;
	}

	public Object pull(String key)
		throws IOException
	{
		return pull(new DQuery(document).find(key), null);
	}

	public <T> T pull(String key, Class<T> clazz)
		throws IOException
	{
		return clazz.cast(pull(new DQuery(document).find(key), clazz));
	}

	private Object pull(DQuery dq, Class<?> clazz)
		throws IOException
	{
		if (dq.isNull()) {
			return null;
		}

		try {
			Class<?> implClass = clazz;
			if (!dq.find("$class").isNull()) {
				String className = dq.find("$class").require(String.class);
				implClass = Class.forName(className);
			}

			if (implClass == null || implClass.isInstance(A_MAP)) {
				return applyProperties(dq.require(Map.class), new HashMap<String,Object>());
			}

			if (implClass.isInterface()) {
				throw new RuntimeException(implClass.getName() + ": is an interface");
			}

			ConfigurationDescriptor configDesc = ConfigurationDescriptor.analyze(implClass);
			if (configDesc != null) {
				Object config = configDesc.getConfiguratorClass().newInstance();
				applyProperties(dq.require(Map.class), new BeanPropertyMap(config));
				return configDesc.getConstructor().newInstance(config);
			}

			Object obj = implClass.newInstance();
			applyProperties(dq.require(Map.class), new BeanPropertyMap(obj));
			return obj;
		}
		catch (Exception e) {
			throw new IOException("cannot configure " + dq.getPath(), e);
		}
	}

	private Map<String,Object> applyProperties(Map<String,Object> config, Map<String,Object> target)
	{
		for (Map.Entry<String,Object> entry : config.entrySet()) {
			if (!entry.getKey().startsWith("$")) {
				target.put(entry.getKey(), entry.getValue());
			}
		}
		return target;
	}
}
