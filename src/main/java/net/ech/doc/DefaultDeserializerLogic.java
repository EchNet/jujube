package net.ech.doc;

import java.io.IOException;

class DefaultDeserializerLogic
{
	public final static String JSON_CONTENT_TYPE = "application/json";
	public final static String YAML_CONTENT_TYPE = "application/yml";

	public static Deserializer createDeserializer(DocumentSource documentSource)
		throws IOException
	{
		String mimeType = documentSource.getMimeType();
		if (mimeType == null || JSON_CONTENT_TYPE.equals(mimeType)) {
			return new JsonDeserializer();
		}
		else if (YAML_CONTENT_TYPE.equals(mimeType)) {
			return new YamlDeserializer();
		}
		else {
			throw new DocumentException(documentSource.toString() + ": invalid document mime type " + mimeType);
		}
	}
}
