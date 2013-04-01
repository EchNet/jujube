package net.ech.doc;

import java.io.IOException;

//
// A DocumentLoader resolves a key to the location of a document and loads that document into memory.
//
public interface DocumentLoader
{
	public Document load(String key)
		throws IOException;
}
