package net.ech.doc;

import java.io.IOException;
import java.io.Reader;
import org.yaml.snakeyaml.Yaml;

/**
 * A really simple YML deserializer.
 */
public class YamlDeserializer
	implements Deserializer
{
	@Override
	public Object deserialize(Reader input)
		throws IOException
	{
		Yaml yaml = new Yaml();
		return yaml.load(input);
	}
}
