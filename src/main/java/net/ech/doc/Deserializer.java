package net.ech.doc;

import java.io.IOException;
import java.io.Reader;

public interface Deserializer
{
	public Object deserialize(Reader input)
		throws IOException;
}

