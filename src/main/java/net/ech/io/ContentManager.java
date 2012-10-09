package net.ech.io;

import java.io.*;
import java.net.*;
import java.util.*;

public class ContentManager
	implements ContentSource
{
	private Map<String,ContentSource> authorities;
	private Map<String,String> mappings;

    public ContentManager(Map<String,ContentSource> authorities, Map<String,String> mappings)
		throws IOException
	{
		this.authorities = authorities;
		this.mappings = mappings;
	}

	/**
	 * @inheritDoc
	 */
    public ContentHandle resolve(ContentRequest request)
        throws IOException
	{
		return questionAuthority(request.getPath()).resolve(request);
	}

	/**
	 * @inheritDoc
	 */
    public Object[] list(String path)
        throws IOException
	{
		return questionAuthority(path).list(path);
	}

	private ContentSource questionAuthority(String uriString)
		throws IOException
	{
		try {
			URI uri = new URI(uriString);
			String authority = uri.getAuthority();
			if (authority == null) {
				throw new IOException(uriString + ": authority required");
			}
			if (!authorities.containsKey(authority)) {
				throw new IOException(uriString + ": unknown authority");
			}
			return authorities.get(authority);
		}
		catch (URISyntaxException e) {
			throw new IOException(uriString, e);
		}
	}
}
