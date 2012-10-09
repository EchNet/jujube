package net.ech.io;

import net.ech.io.template.*;
import java.io.*;
import java.util.*;

public class AggregateJsonContentQuery
	implements ContentQuery
{
	private List<ContentQuery> children = new ArrayList<ContentQuery>();

	public void addChild(ContentQuery child)
	{
		this.children.add(child);
	}

	@Override
    public ContentHandle query()
        throws IOException
	{
		switch (children.size())
		{
		case 0:
			return new JsonContentHandle(null);
		case 1:
			return children.get(0).query();
		default:
			return new TemplateContentHandle(createListTemplate(), new ChildEvaluator());
		}
	}

	private JsonContentHandle createListTemplate()
	{
		List<Object> list = new ArrayList<Object>();
		for (int ix = 0; ix < children.size(); ++ix) {
			list.add("{{" + ix + "}}");
		}
		return new JsonContentHandle(list);
	}

	private class ChildEvaluator
		implements Evaluator
	{
		@Override
		public ContentHandle evaluateExpression(String expr)
			throws IOException
		{
			int index = Integer.parseInt(expr);
			ContentQuery child = children.get(index);
			ContentHandle contentHandle = child.query();
			if (!ContentTypes.isJson(contentHandle.getCodec().getContentType())) {
				throw srcContentTypeError(contentHandle);
			}
			return contentHandle;
		}
	}

	private IOException srcContentTypeError(ContentHandle contentHandle)
	{
		String contentType = null;
		try {
			contentType = contentHandle.getCodec().getContentType();
		}
		catch (IOException e) {}
		String src = contentHandle.getSource();
		if (src == null) {
			src = contentHandle.toString();
		}
		return new IOException(src + ": content not JSON (" + contentType + ")");
	}
}
