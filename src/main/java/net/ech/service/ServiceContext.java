package net.ech.service;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import net.ech.nio.ItemHandle;

public interface ServiceContext
{
	public HttpServlet getServlet();

	public HttpServletRequest getRequest();

	public HttpServletResponse getResponse();

	public void submitContent(ItemHandle contentItemHandle)
		throws ServletException;
}
