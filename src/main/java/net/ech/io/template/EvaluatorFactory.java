package net.ech.io.template;

import net.ech.io.ContentRequest;
import java.io.IOException;

public interface EvaluatorFactory
{
	public Evaluator createEvaluator(ContentRequest request)
		throws IOException;
}
