package net.ech.service;

import net.ech.config.*;
import net.ech.codec.*;
import net.ech.io.*;
import net.ech.io.template.*;
import net.ech.util.*;
import java.io.IOException;
import java.net.URLEncoder;

public class TemplateExpressionEvaluator
	implements Evaluator
{
	private ContentRequestManager contentRequestManager;
	private ContentManager contentManager;

	/**
	 * Constructor.
	 * @param request The request is the source of parameters and attributes.
	 */
	public TemplateExpressionEvaluator(ContentRequest request, ContentManager contentManager)
	{
		this.contentRequestManager = new ContentRequestManager(request);
		this.contentManager = contentManager;
	}

	@Override
	public ContentHandle evaluateExpression(String expr)
		throws IOException
	{
		TemplateExpressionParser.Node parsed = new TemplateExpressionParser(expr).parse();
		return parsed == null ? new TextContentHandle("") : eval(parsed).toContentHandle();
	}

	private static abstract class EvalNode
	{
		public EvalNode index(String name)
			throws IOException
		{
			return new DocumentNode(null);
		}

		public EvalNode call(EvalNode args)
			throws IOException
		{
			throw new IOException("not a function");
		}

		public ContentHandle toContentHandle()
			throws IOException
		{
			throw new IOException("not a value");
		}
	}

	private static class ContentHandleNode extends EvalNode
	{
		ContentHandle contentHandle;

		public ContentHandleNode(ContentHandle contentHandle)
		{
			this.contentHandle = contentHandle;
		}

		public EvalNode index(String name)
			throws IOException
		{
			return new DocumentNode(contentHandle.getDocument()).index(name);
		}

		public ContentHandle toContentHandle()
			throws IOException
		{
			return contentHandle;
		}
	}

	private static class DocumentNode extends EvalNode
	{
		Object document;

		public DocumentNode(Object document)
		{
			this.document = document;
		}

		public EvalNode index(String name)
			throws IOException
		{
			Object member = new DQuery(document).find(new DPath(name)).get();
			if (member instanceof EvalNode)
				return (EvalNode) member;
			return new DocumentNode(member);
		}

		public ContentHandle toContentHandle()
			throws IOException
		{
			return new JsonContentHandle(document);
		}
	}

	private class LoadNode extends EvalNode
	{
		@Override
		public EvalNode call(EvalNode args) 
			throws IOException
		{
			if (args == null) {
				throw new IOException("load requires arg");
			}
			String str = args.toContentHandle().getDocument().toString();
			return new ContentHandleNode(contentManager.resolve(getContentRequest().withPath(str)));
		}
	}

	private class UrlEncodeNode extends EvalNode
	{
		@Override
		public EvalNode call(EvalNode args) 
			throws IOException
		{
			if (args == null) {
				throw new IOException("urlEncode requires arg");
			}
			String str = args.toContentHandle().getDocument().toString();
			return new DocumentNode(URLEncoder.encode(str, "UTF-8"));
		}
	}

	private EvalNode eval(TemplateExpressionParser.Node root)
		throws IOException
	{
		switch (root.ntype) {
		case TemplateExpressionParser.N_STRING:
			return new DocumentNode(root.sval);
		case TemplateExpressionParser.N_THIS:
			return new DocumentNode(getRootContext());
		case TemplateExpressionParser.N_MEMBER:
			return eval(root.lhs).index(root.rhs.sval);
		case TemplateExpressionParser.N_FUNCTION:
			return eval(root.lhs).call(root.rhs == null ? null : eval(root.rhs));
		case TemplateExpressionParser.N_PLUS:
			return concat(eval(root.lhs), eval(root.rhs));
		default:
			throw new RuntimeException("should not be reached");
		}
	}

	private EvalNode concat(EvalNode n1, EvalNode n2)
		throws IOException
	{
		String str1 = toString(n1.toContentHandle().getDocument());
		String str2 = toString(n2.toContentHandle().getDocument());
		return new DocumentNode(str1 + str2);
	}

	private static String toString(Object obj)
	{
		return obj == null ? "null" : obj.toString();
	}

	private Object getRootContext()
		throws IOException
	{
		return new Hash()
			.addEntry("getCurrentTimeMillis", new EvalNode() {
				@Override
				public EvalNode call(EvalNode arg) throws IOException
				{
					return new DocumentNode(System.currentTimeMillis());
				}
			})
			.addEntry("load", new LoadNode())
			.addEntry("pathInfo", contentRequestManager.getContentRequest().getPath())
			.addEntry("httpRequest", new Hash()
				.addEntry("path", contentRequestManager.getRequestWrapper().getPath())
				.addEntry("params", contentRequestManager.getRequestWrapper().getParameterMap())
				.addEntry("headers", contentRequestManager.getRequestWrapper().getHeaderMap())
				.addEntry("userAgent", contentRequestManager.getRequest().getHeader("User-Agent"))
			)
			.addEntry("urlEncode", new UrlEncodeNode())
			.addEntry("server", new Hash()
				.addEntry("version", ServiceProperties.getInstance().getSourceCommitId())
				.addEntry("mode", getConfiguration().getString("mode", "???")))
			;
	}

	private ContentRequest getContentRequest()
	{
		return contentRequestManager.getContentRequest();
	}

	private Configuration getConfiguration()
	{
		return contentRequestManager.getConfiguration();
	}
}
