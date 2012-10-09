package net.ech.service;

import net.ech.codec.*;
import net.ech.io.*;
import java.io.*;
import javax.servlet.http.HttpServletRequest;

class RequestContentHandle
	extends AbstractContentHandle
{
	private RequestWrapper requestWrapper;

	public RequestContentHandle(RequestWrapper requestWrapper)
	{
		super("(request)");
		this.requestWrapper = requestWrapper;
	}

	@Override
	public Codec getCodec()
		throws IOException
	{
		return ContentTypes.getDefaultCodec(getContentType(), getCharacterEncoding());
	}

	@Override
	public Object getDocument()
		throws IOException
	{
		InputStream inputStream = getInputStream();
		try {
			return getCodec().decode(inputStream);
		}
		finally {
			inputStream.close();
		}
	}

	@Override
	public void write(OutputStream outputStream)
		throws IOException
	{
		InputStream inputStream = getInputStream();
		try {
			Codec codec = getCodec();
			codec.encode(codec.decode(inputStream), outputStream);
		}
		finally {
			inputStream.close();
		}
	}

	@Override
	public void write(Writer writer)
		throws IOException
	{
		InputStream inputStream = getInputStream();
		try {
			getCodec().write(inputStream, writer);
		}
		finally {
			inputStream.close();
		}
	}

	@Override
	public String getContentType()
	{
		String contentType = requestWrapper.getContentType();
		if (contentType == null) {
			contentType = "application/json";
		}
		return contentType;
	}

	private String getCharacterEncoding()
	{
		String characterEncoding = requestWrapper.getRequest().getCharacterEncoding();
		if (characterEncoding == null) {
			characterEncoding = "UTF-8";   // is this the best choice?
		}
		return characterEncoding;
	}

	private InputStream getInputStream()
		throws IOException
	{
		InputStream input = requestWrapper.getRequest().getInputStream();

		// Throw IOException if content is empty.  Prevents empty log messages.
		int b;
		if (input == null || (b = input.read()) <= 0) {
			throw new IOException("NULL CONTENT");
		}

		// Push that byte back onto the stream!
		PushbackInputStream pushMe = new PushbackInputStream(input); 
		pushMe.unread(b);
		return pushMe;
	}
};
