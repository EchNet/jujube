package com.swoop.test;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public class PrintExecutor
	implements Executor, PrintContext
{
	private Map<String,List<String>> sequences;
	private Map<String,PrintWorker> workers;
	private StringWriter output;

	// Sequences express order of execution.
	// Sequences are looked up by name.
	// Each sequence is a list of worker names.
	public Map<String,List<String>> getSequences()
	{
		return sequences;
	}

	public void setSequences(Map<String,List<String>> sequences)
	{
		this.sequences = sequences;
	}

	// Workers express functionality.
	// Workers are looked up by name.
	public Map<String,PrintWorker> getWorkers()
	{
		return workers;
	}

	public void setWorkers(Map<String,PrintWorker> workers)
	{
		this.workers = workers;
	}

	// Executor method.  Execute the pipeline.
	@Override
	public void run(Map<String,String> parameters)
	{
		String sequenceName = parameters.containsKey("sequence") ? parameters.get("sequence") : "default";
		List<String> sequence = sequences.get(sequenceName);
		if (sequence == null) {
			throw new RuntimeException(this + " was not configured with a sequence named " + sequenceName);
		}
		int count = 0;
		for (String workerName : sequence) {
			PrintWorker worker = workers.get(workerName);
			if (worker == null) {
				throw new RuntimeException(this + " was not configured with a worker named " + workerName);
			}
			if (count++ > 0) {
				print(", ");
			}
			worker.run(this);
		}
	}

	// PrintContext method.
	@Override
	public void print(Object obj)
	{
		output.write(obj.toString());
	}
	
	// PrintContext method.
	@Override
	public void println()
	{
		output.write("\n");
	}

	public String getOutput()
	{
		return output.toString();
	}
}
