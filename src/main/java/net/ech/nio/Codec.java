package net.ech.nio;

import java.io.*;

public interface Codec
{
	public Object decode(Reader reader)
		throws IOException;

	public void encode(Object obj, Writer writer)
		throws IOException;
}
