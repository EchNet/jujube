package net.ech.nio;

import java.io.File;

class FileResourceConfig
{
	protected File base;
	protected String extension;
	protected boolean ignoreQueryExtension;
	protected int cachePeriod;
	protected String mimeType;
	protected String characterEncoding = FileResource.DEFAULT_CHARACTER_ENCODING;

	public FileResourceConfig()
	{
	}

	public FileResourceConfig(FileResourceConfig that)
	{
		this.base = that.base;
		this.extension = that.extension;
		this.ignoreQueryExtension = that.ignoreQueryExtension;
		this.cachePeriod = that.cachePeriod;
		this.mimeType = that.mimeType;
		this.characterEncoding = that.characterEncoding;
	}
}
