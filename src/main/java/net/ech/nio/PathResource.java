package net.ech.nio;

import java.io.*;
import java.util.*;

public class PathResource
	implements Resource
{
	private List<Resource> children;

	public static class Config
	{
		private List<Resource> children = new ArrayList<Resource>();

		public void addResource(Resource resource)
		{
			this.children.add(resource);
		}
	}

	public PathResource(Config config)
	{
		this.children = config.children;
	}

	@Override
	public ItemHandle resolve(Query query)
		throws IOException
	{
		if (children.size() == 0) {
			throw new IOException("empty source path");
		}

		for (Resource child : children) {
			try {
				return child.resolve(query);
			}
			catch (FileNotFoundException e) {
			}
		}

		throw new FileNotFoundException(query.toString());
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		for (Resource child : children) {
			if (buf.length() > 0) {
				buf.append(":");
			}
			buf.append(child.toString());
		}
		return "[" + buf.toString() + "]";
	}
}
