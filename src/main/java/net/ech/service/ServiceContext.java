package net.ech.service;

import javax.servlet.http.*;
import net.ech.nio.ItemHandle;

public interface ServiceContext
{
	public HttpServlet getServlet();

	public HttpServletRequest getRequest();

	public HttpServletResponse getResponse();

	public void setQueryPath(String queryPath);
	public String getQueryPath();

	public void setContent(ItemHandle contentItem);
	public ItemHandle getContent();
}
