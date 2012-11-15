package net.ech.service;

import javax.servlet.ServletException;
import net.ech.nio.ItemHandle;

public interface ServiceModule
{
	public void setServiceContext(ServiceContext context);
	public void preprocess()
		throws ServletException;
	public void process()
		throws ServletException;
	public void postprocess()
		throws ServletException;
}
