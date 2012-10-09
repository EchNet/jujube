package net.ech.io.template;

import net.ech.io.ContentHandle;
import java.io.IOException;

public interface Evaluator
{
	public ContentHandle evaluateExpression(String expr)
		throws IOException;
}
