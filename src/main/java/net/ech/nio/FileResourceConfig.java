package net.ech.nio;

import java.io.File;

class FileResourceConfig
{
	protected File base;
	protected String extension;
	protected boolean ignoreQueryExtension;
	protected String mimeType;
	protected String characterEncoding = FileResource.DEFAULT_CHARACTER_ENCODING;
	protected Long cachePeriod;

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
		this.cachePeriod = cachePeriod;
	}
}
