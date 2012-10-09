package net.ech.service;

import net.ech.io.*;

public class PhonyController
	implements ContentQuery
{
	RequestWrapper requestWrapper;
	ContentRequest contentRequest;

	public PhonyController(RequestWrapper requestWrapper, ContentRequest contentRequest)
	{
		this.requestWrapper = requestWrapper;
		this.contentRequest = contentRequest;
	}

	public RequestWrapper getRequestWrapper()
	{
		return requestWrapper;
	}

	public ContentRequest getContentRequest()
	{
		return contentRequest;
	}

	public ContentHandle query()
	{
		return new TextContentHandle("phony");
	}
}
