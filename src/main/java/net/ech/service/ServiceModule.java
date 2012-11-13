package net.ech.service;

import net.ech.nio.ItemHandle;

public interface ServiceModule
{
	public void serviceStarted();
	public void contentReceived();
	public void contentReady();
}
