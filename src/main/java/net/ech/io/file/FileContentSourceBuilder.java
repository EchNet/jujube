package net.ech.io.file;

import net.ech.config.*;
import net.ech.io.*;
import net.ech.util.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class FileContentSourceBuilder 
    extends AbstractBuilder<ContentSource>
{
    public FileContentSourceBuilder(Configuration configuration)
	{
		super(configuration);
	}

	@Override
	public Class<ContentSource> getClientClass()
	{
		return ContentSource.class;
	}

	@Override
    protected ContentSource buildByType(DQuery dq, String type)
        throws IOException
    {
		AbstractFileContentSource contentSource;

		String path = dq.find("path").cast(String.class, null);
		String url = dq.find("url").cast(String.class, null);

		if ((path == null) == (url == null)) {
			throw new DocumentException(dq.getPath() + ": exactly one of (url, path) must be specified");
		}

		/**  Hmmm....
		if (url != null && url.startsWith("file:")) {
			path = url.substring(5);
		}
		**/

		contentSource = path != null ? new FileContentSource(new File(path)) : new UrlContentSource(new URL(url));

		contentSource.setStatic(dq.find("static").cast(Boolean.class, false));
		contentSource.setStripExtension(dq.find("stripExtension").cast(Boolean.class, false));
		contentSource.setExtension(dq.find("extension").cast(String.class, null));
		String contentType = dq.find("contentType").cast(String.class, null);
		String characterEncoding = dq.find("characterEncoding").cast(String.class, null);
		if (contentType != null || characterEncoding != null) {
			contentSource.setCodec(ContentTypes.getDefaultCodec(contentType, characterEncoding));
		}

		return contentSource;
	}
}
