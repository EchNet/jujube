package net.ech.io;

import net.ech.io.template.*;
import net.ech.codec.*;
import net.ech.util.*;
import java.io.*;
import java.util.*;

public class JsonpWrapperContentSource
	extends TemplateContentSource
	implements ContentSource
{
	private static final String DEFAULT_CALLBACK = "SPX.handle";

	public JsonpWrapperContentSource(ContentSource inner)
	{
		super(new JsonpTemplateSource(), new MyEvaluatorFactory(inner));
	}

	private static class JsonpTemplateSource
		extends AbstractContentSource
	{
		@Override
		public ContentHandle resolve(ContentRequest request)
			throws IOException
		{
			String callback = DEFAULT_CALLBACK;
			if (request.getParameters().containsKey("callback")) {
				callback = request.getParameters().get("callback").toString();
			}
			return new BufferedContentHandle(
				request.getPath(),
				new TextCodec(ContentTypes.JAVASCRIPT_CONTENT_TYPE),
				callback + "(\"{{CONTENT}}\")");
		}
	}

	public static class MyEvaluatorFactory
		implements EvaluatorFactory
	{
		private ContentSource inner;

		MyEvaluatorFactory(ContentSource inner)
		{
			this.inner = inner;
		}

		@Override
		public Evaluator createEvaluator(final ContentRequest request)
			throws IOException
		{
			return new StaticEvaluator(inner.resolve(request));
		}
	}
}
