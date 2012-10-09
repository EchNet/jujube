package net.ech.io.file;

import net.ech.codec.*;
import net.ech.io.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class FileContentSource
	extends AbstractFileContentSource
	implements ContentSource
{
	private File base;

	public FileContentSource(String base)
	{
		this(new File(base));
	}

	public FileContentSource(File base)
	{
		this.base = base;
	}

	public FileContentSource(File base, boolean mStatic)
	{
		super(mStatic);
		this.base = base;
	}

	public File getBase()
	{
		return base;
	}

	@Override
    protected AbstractContentHandle resolveUri(URI uri, ContentRequest request)
        throws IOException
	{
		// If path is empty, use last component of base as path.
		String path = uri.getPath();
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		File file = path.length() == 0 ? new File(processPath(base.getPath())) : new File(base, processPath(path));
		if (!file.canRead()) {
			throw new FileNotFoundException(file.toString());
		}

		Codec codec = getCodec();
		if (codec == null) {
			codec = getDefaultCodec(pluckExtension(file.toString()));
		}

		return new FileContentHandle(file, codec);
	}

	@Override
    public Object[] list(String path)
        throws IOException
	{
		File file = new File(base, path);
		if (!file.exists()) {
			throw new FileNotFoundException();
		}

		final String extension = getExtension();

		if (!file.isDirectory()) {
			throw new IOException(file + ": not a directory");
		}

		// List files.  If there is an implicit extension, list only files having that extension.
		String[] listing = new File(base, path).list(new FilenameFilter() {
			public boolean accept (File dir, String name) {
				return extension == null || name.endsWith(extension);
			}
		});

		// An error condition that's difficult to produce:
		if (listing == null) {
			throw new IOException(file + ": cannot read");
		}

		// Remove implicit extensions.
		if (extension != null) {
			for (int i = 0; i < listing.length; ++i) {
				listing[i] = listing[i].substring(0, listing[i].length() - extension.length());
			}
		}

		// Sort array, to eliminate test failures resulting from varying directory orderings!
		Arrays.sort(listing);
		return listing;
	}

	@Override
	public String toString()
	{
		return "FILE:" + base;
	}

	private static String pluckExtension(String fileName)
	{
		int dot = fileName.lastIndexOf('.');
		return dot < 0 ? "" : fileName.substring(dot);
	}

	private final static Map<String,Codec> CODEX = new HashMap<String,Codec>();
	static
	{
		CODEX.put(".css", new TextCodec("text/css"));
		CODEX.put(".txt", new TextCodec("text/plain"));
		CODEX.put(".js", new TextCodec(ContentTypes.JAVASCRIPT_CONTENT_TYPE));
		CODEX.put(".json", new JsonCodec());
		CODEX.put(".html", new TextCodec(ContentTypes.HTML_CONTENT_TYPE));
		CODEX.put(".png", new BinaryCodec("image/png"));
		CODEX.put(".gif", new BinaryCodec("image/gif"));
		CODEX.put(".xml", new TextCodec(ContentTypes.XML_CONTENT_TYPE));
	}

	private Codec getDefaultCodec(String extension)
	{
		extension = extension.toLowerCase();
		if (CODEX.containsKey(extension)) {
			return CODEX.get(extension);
		}
		return new BinaryCodec();
	}
}
