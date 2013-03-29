package com.swoop.test;

public class PrintlnWorker
	implements PrintWorker
{
	@Override
	public void run(PrintContext context)
	{
		context.println();
	}
}
