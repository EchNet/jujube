package net.ech.doc;

import java.io.IOException;

public class FileDocumentResolver
	implements DocumentResolver
{
	@Override
	public DocumentProducer resolve(String key)
		throws IOException
	{
		return new FileDocumentProducer(key, addJsonExt(key));
	}

	private static String addJsonExt(String name)
	{
		if (!name.endsWith(".json")) {
			name += ".json";
		}
		return name;
	}
}
