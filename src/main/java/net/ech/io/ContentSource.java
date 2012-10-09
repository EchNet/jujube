package net.ech.io;

import java.io.IOException;
import java.util.Map;

/**
 * Common interface to any service that resolves a parameterized path reference to a content item.
 */
public interface ContentSource
{
	/**
	 * Resolve the given path and parameters to a content resource.
	 * @throws FileNotFoundException if the path does not identify a content resource
	 */
    public ContentHandle resolve(ContentRequest request)
        throws IOException;

    public Object[] list(String path)
        throws IOException;
}
