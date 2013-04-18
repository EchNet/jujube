package net.ech.doc;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import com.fasterxml.jackson.core.JsonFactory;
import static com.fasterxml.jackson.core.JsonParser.Feature.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A really simple JSON deserializer based on Jackson.
 */
public class JsonDeserializer
{
	private JsonFactory jsonFactory;

	public JsonDeserializer()
	{
		jsonFactory = new JsonFactory();
		jsonFactory.setCodec(new ObjectMapper() {
		});
		jsonFactory.enable(ALLOW_UNQUOTED_FIELD_NAMES);
		jsonFactory.enable(ALLOW_COMMENTS);
	}

	public Object read(Reader input)
		throws IOException
	{
		return jsonFactory.createJsonParser(input).readValueAs(Object.class);
	}

	public Object decode(String text)
		throws IOException
	{
		return read(new StringReader(text));
	}
}
