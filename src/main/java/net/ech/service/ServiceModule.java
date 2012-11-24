package net.ech.service;

import java.io.IOException;
import javax.servlet.ServletException;

public interface ServiceModule
{
	public void setMethod(String methodRegexp);
	public String getMethod();

	public void setPath(String path);
	public String getPath();

	public void process(ServiceContext context)
		throws IOException, ServletException;
}
