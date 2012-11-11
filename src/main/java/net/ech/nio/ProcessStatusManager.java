package net.ech.nio;

public class ProcessStatusManager
{
	private ProcessStatus processStatus = ProcessStatus.INITIAL;

	public synchronized ProcessStatus getProcessStatus()
	{
		return processStatus;
	}

	public synchronized void running()
	{
		if (processStatus != ProcessStatus.INITIAL) {
			throw new IllegalStateException("already run");
		}
		processStatus = ProcessStatus.RUNNING;
	}

	public synchronized void terminal()
	{
		processStatus = ProcessStatus.TERMINAL;
	}
}
