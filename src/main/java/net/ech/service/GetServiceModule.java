package net.ech.service;

import net.ech.nio.*;
import java.io.*;
import javax.servlet.http.*;

public class GetServiceModule
	extends AbstractServiceModule
	implements ServiceModule
{
	public GetServiceModule(ServiceContext serviceContext)
	{
		super(serviceContext);
	}

	@Override
	public void serviceStarted()
	{
	}
}
