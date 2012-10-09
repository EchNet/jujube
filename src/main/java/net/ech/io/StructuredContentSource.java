package net.ech.io;

import net.ech.util.*;
import net.ech.service.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class StructuredContentSource
	extends AbstractContentSource
{
	private Map<String,ContentSource> structure;

	public StructuredContentSource(Map<String,ContentSource> structure)
	{
		this.structure = structure;
	}

	@Override
	public ContentHandle resolve(ContentRequest request)
		throws IOException
	{
		Resolution resolution = doResolve(request.getPath());
		if (resolution.contentSource == null) {
			throw new IOException(request.getPath() + ": access denied");
		}

		return resolution.contentSource.resolve(new ContentRequest(request).withPath(resolution.remainingPath));
	}

	@Override
	public Object[] list(String path)
		throws IOException
	{
		Resolution resolution = doResolve(path);
		if (resolution.contentSource == null) {
			return structure.keySet().toArray(new String[0]);
		}
		else {
			return resolution.contentSource.list(resolution.remainingPath);
		}
	}

	private static class Resolution
	{
		ContentSource contentSource;
		String remainingPath;

		Resolution(ContentSource contentSource, String remainingPath)
		{
			this.contentSource = contentSource;
			this.remainingPath = remainingPath;
		}
	}

	private Resolution doResolve(String path)
		throws IOException
	{
		ContentSource contentSource = null;
		String remainingPath = path;
		String[] pathComps = path.split("\\/");
		if (pathComps.length > 1 || pathComps.length > 0 && !pathComps[0].equals("")) {
			int pcx = 0;
			String comp = pathComps[pcx++];
			if (comp.equals("")) comp = pathComps[pcx++];
			contentSource = structure.get(comp);
			if (contentSource == null) {
				throw new FileNotFoundException(path + ": not found");
			}
			remainingPath = reconcat(pathComps, pcx);
		}

		return new Resolution(contentSource, remainingPath);
	}

	private static String reconcat(String[] comps, int start)
	{
		StringBuilder buf = new StringBuilder();

		for (int i = start; i < comps.length; ++i) {
			if (buf.length() > 0) {
				buf.append('/');
			}
			buf.append(comps[i]);
		}

		return buf.toString();
	}
}
