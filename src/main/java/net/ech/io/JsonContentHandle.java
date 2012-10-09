package net.ech.io;

import net.ech.codec.*;
import java.io.*;

/**
 * Frequently used.
 */
public class JsonContentHandle
	extends BufferedContentHandle
	implements ContentHandle
{
	private final static JsonCodec JSON_CODEC = new JsonCodec();

	public JsonContentHandle(Object document)
	{
		super("(json)", JSON_CODEC, document);
	}

	public JsonContentHandle(String source, Object document)
	{
		super(source, JSON_CODEC, document);
	}
}
