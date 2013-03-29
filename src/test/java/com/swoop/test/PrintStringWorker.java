package com.swoop.test;

public class PrintStringWorker
	implements PrintWorker
{
	private String string;

	public String getString()
	{
		return string;
	}

	public void setString(String string)
	{
		this.string = string;
	}

	@Override
	public void run(PrintContext context)
	{
		context.print(string);
	}
}
