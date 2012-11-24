package net.ech.service;

import java.io.IOException;
import javax.servlet.ServletException;

public interface ServicePostProcessor
{
	public void postprocess(ServiceContext context)
		throws IOException, ServletException;
}
