package com.swoop.test;

import java.util.Map;

public interface Executor
{
	public Executor run();
	public Executor run(Map<String,String> parameters);
}
