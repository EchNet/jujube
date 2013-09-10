package net.ech.doc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class FileDocumentSource
	implements DocumentSource
{
	private String key;
	private String path;
	private String mimeType;

	public FileDocumentSource(String key)
		throws DocumentException
	{
		this.key = key;
		parseKey();
	}

	private void parseKey()
		throws DocumentException
	{
		this.path = key;

		// Strip scheme
		int colon = path.indexOf(':');
		if (colon > 0) {
			path = path.substring(colon + 1);
		}

		// Look at file name extension.
		int dot = path.lastIndexOf('.');
		if (dot < 0) {
			this.path += ".json";
			this.mimeType = DefaultDeserializerLogic.JSON_CONTENT_TYPE;
		}
		else {
			String extension = path.substring(dot);
			if (".json".equals(extension)) {
				this.mimeType = DefaultDeserializerLogic.JSON_CONTENT_TYPE;
			}
			else if (".yml".equals(extension)) {
				this.mimeType = DefaultDeserializerLogic.YAML_CONTENT_TYPE;
			}
			else {
				throw new DocumentException(this + ": unrecognized file name extension " + extension);
			}
		}
	}

	public String getPath()
	{
		return path;
	}

	@Override
	public Reader open()
		throws IOException
	{
		File f = new File(path);
		if (!f.exists()) {
			throw new FileNotFoundException(toString());
		}
		if (!f.canRead()) {
			throw new IOException(toString() + ": cannot read");
		}
		return new BufferedReader(new FileReader(f));
	}

	@Override
	public String getMimeType()
	{
		return mimeType;
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		buf.append(path);
		if (!key.equals(path)) {
			buf.append(" (");
			buf.append(key);
			buf.append(")");
		}
		return buf.toString();
	}
}
