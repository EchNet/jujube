package net.ech.service;

import javax.servlet.http.*;

public class HubServlet
	extends HttpServlet
{
    @Override
	public void service(HttpServletRequest request, HttpServletResponse response)
    {
		// Turn it inside out.
		new HubService(this, request, response).run();
	}
}
