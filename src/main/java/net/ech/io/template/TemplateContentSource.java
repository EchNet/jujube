package net.ech.io.template;

import net.ech.io.*;
import java.io.IOException;

/**
 * For lack of a better name...
 */
public class TemplateContentSource
	extends ProxyContentSource
	implements ContentSource
{
	private EvaluatorFactory evaluatorFactory;

	public TemplateContentSource(ContentSource inner, Evaluator evaluator)
	{
		this(inner, new StaticEvaluatorFactory(evaluator));
	}

	public TemplateContentSource(ContentSource inner, EvaluatorFactory evaluatorFactory)
	{
		super(inner);
		this.evaluatorFactory = evaluatorFactory;
	}

	@Override
    public ContentHandle resolve(ContentRequest request)
        throws IOException
	{
		// The request can potentially factor into both the template and the template evaluation context.
		return new TemplateContentHandle(super.resolve(request), evaluatorFactory.createEvaluator(request));
	}
}
