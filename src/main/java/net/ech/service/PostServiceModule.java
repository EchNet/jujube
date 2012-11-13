package net.ech.service;

import net.ech.nio.*;
import java.io.*;
import javax.servlet.http.*;

public class PostServiceModule
	extends AbstractServiceModule
	implements ServiceModule
{
	public PostServiceModule(ServiceContext serviceContext)
	{
		super(serviceContext);
	}

	@Override
	public void serviceStarted()
	{
	}
}
