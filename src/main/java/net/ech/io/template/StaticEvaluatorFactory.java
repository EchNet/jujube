package net.ech.io.template;

import net.ech.io.ContentRequest;
import java.io.IOException;

/**
 * Trivial EvaluatorFactory implementation.
 */
public class StaticEvaluatorFactory
	implements EvaluatorFactory
{
	private Evaluator evaluator;

	public StaticEvaluatorFactory(Evaluator evaluator)
	{
		this.evaluator = evaluator;
	}

	@Override
	public Evaluator createEvaluator(ContentRequest request)
		throws IOException
	{
		return evaluator;
	}
}
