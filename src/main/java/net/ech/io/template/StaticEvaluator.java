package net.ech.io.template;

import net.ech.io.ContentHandle;
import java.io.IOException;

/**
 * Simplest possible implementation of Evaluator interface.
 */
public class StaticEvaluator
	implements Evaluator
{
	private ContentHandle contentHandle;

	public StaticEvaluator(ContentHandle contentHandle)
	{
		this.contentHandle = contentHandle;
	}

	@Override
	public ContentHandle evaluateExpression(String expr)
		throws IOException
	{
		return contentHandle;
	}
}
