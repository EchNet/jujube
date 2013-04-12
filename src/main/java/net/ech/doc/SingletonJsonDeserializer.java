package net.ech.doc;

/**
 * Maintain an application-wide instance of JsonDeserializer.
 */
public class SingletonJsonDeserializer
{
	private static JsonDeserializer instance = new JsonDeserializer();

	public static JsonDeserializer getInstance()
	{
		return instance;
	}

	public static void setInstance(JsonDeserializer instance)
	{
		SingletonJsonDeserializer.instance = instance;
	}
}
