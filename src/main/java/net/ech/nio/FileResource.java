package net.ech.nio;

import java.io.*;
import java.net.*;
import java.util.*;

public class FileResource
	extends FileResourceConfig
	implements Resource
{
	public final static String DEFAULT_MIME_TYPE = "text/plain";
	public final static String DEFAULT_CHARACTER_ENCODING = "UTF-8";

	public static class Config
		extends FileResourceConfig
	{
		public Config(String base)
		{
			this(new File(base));
		}

		public Config(File base)
		{
			this.base = base;
		}

		public void setExtension(String extension)
		{
			this.extension = extension;
		}

		public void setIgnoreQueryExtension(boolean ignoreQueryExtension)
		{
			this.ignoreQueryExtension = ignoreQueryExtension;
		}

		public void setMimeType(String mimeType)
		{
			this.mimeType = mimeType;
		}
	}

	private final static Map<String,String> MIME_TYPES_BY_EXTENSION = new HashMap<String,String>();
	static
	{
		try {
			InputStream in = FileResource.class.getClassLoader().getResourceAsStream("net/ech/nio/mimeTypes.properties");
			if (in != null) {
				try {
					Properties properties = new Properties();
					properties.load(in);
					// Create a non-synchronized form of the map.
					for (Map.Entry<Object,Object> entry : properties.entrySet()) {
						MIME_TYPES_BY_EXTENSION.put(entry.getKey().toString(), entry.getValue().toString());
					}
				}
				finally {
					in.close();
				}
			}
		}
		catch (IOException e) {
			// Missing net/ech/nio/mimeTypes.properties?
		}
	}

	public FileResource(Config config)
	{
		super(config);
	}

	@Override
    public ItemHandle resolve(Query query)
        throws IOException
	{
		// If path is empty, use last component of base as path.
		String path = query.getPath();
		File file = path.length() == 0 ? new File(processPath(base.getPath())) : new File(base, processPath(path));
		if (file.isDirectory()) {
			throw new IOException(file + ": is directory");
		}
		if (!file.canRead()) {
			throw new FileNotFoundException(file.toString());
		}
		return new FileItemHandle(file);
	}

	@Override
	public String toString()
	{
		return base.getPath();
	}

	private String processPath(String path)
	{
		if (ignoreQueryExtension) {
			int slash = path.lastIndexOf('/');
			int dot = path.lastIndexOf('.');
			if (dot > slash) {
				path = path.substring(0, dot);
			}
		}
		if (extension != null) {
			path += extension;
		}
		return path;
	}

	private class FileItemHandle
		extends AbstractItemHandle
		implements ItemHandle
	{
		private File file;

		public FileItemHandle(File file)
		{
			this.file = file;
		}

		@Override
		public InputStream openInputStream()
			throws IOException
		{
			return new FileInputStream(file);
		}

		@Override
		public Reader openReader()
			throws IOException
		{
			return new InputStreamReader(openInputStream(), characterEncoding);
		}

		@Override
		public String toString()
		{
			return file.getPath();
		}

		@Override
		public Metadata getMetadata()
		{
			final String mimeType = getMimeType();

			return new Metadata() {

				public String getMimeType() 
				{
					return mimeType;
				}

				public String getCharacterEncoding()
				{
					return characterEncoding;
				}
			};
		}

		private String getMimeType()
		{
			String mimeType = FileResource.this.mimeType;
			if (mimeType == null) {
				String fileName = file.getPath();
				int dot = fileName.lastIndexOf('.');
				String extension = dot < 0 ? "" : fileName.substring(dot);
				mimeType = (String) MIME_TYPES_BY_EXTENSION.get(extension.toLowerCase());
				if (mimeType == null) {
					mimeType = DEFAULT_MIME_TYPE;
				}
			}
			return mimeType;
		}
	}
}
