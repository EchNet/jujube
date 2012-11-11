package net.ech.nio;

import net.ech.codec.*;
import java.io.*;

/**
 * 
 */
public interface Resource
{
	/**
	 * A human-readable description of this resource, e.g. a URI.  May be null.
	 */
	public String toString();

	/**
	 * @throws FileNotFoundException if the query fails because the item identified by the path does not exist
	 */
    public ItemHandle resolve(Query query)
        throws IOException;
}
