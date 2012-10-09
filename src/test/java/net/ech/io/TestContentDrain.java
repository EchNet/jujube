package net.ech.io;

import java.io.*;
import java.util.*;

public class TestContentDrain implements ContentDrain
{
	public List<Object> log = new ArrayList<Object>();

	private boolean doStream;

	public TestContentDrain()
	{
	}

	public TestContentDrain(boolean doStream)
	{
		this.doStream = doStream;
	}

	@Override 
	public ContentHandle accept(ContentHandle stuff)
		throws IOException
	{
		Object doc = doStream ? streamIt(stuff) : stuff.getDocument();
		log.add(doc);
		return stuff;
	}

	private Object streamIt(ContentHandle stuff)
		throws IOException
	{
		StringWriter writer = new StringWriter();
		stuff.write(writer);
		return writer.toString();
	}
}
