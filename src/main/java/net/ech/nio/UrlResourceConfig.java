package net.ech.nio;

import java.net.URL;

class UrlResourceConfig
{
	protected URL base;
	protected String extension;
	protected boolean ignoreQueryExtension;

	public UrlResourceConfig()
	{
	}

	public UrlResourceConfig(UrlResourceConfig that)
	{
		this.base = that.base;
		this.extension = that.extension;
		this.ignoreQueryExtension = that.ignoreQueryExtension;
	}
}
