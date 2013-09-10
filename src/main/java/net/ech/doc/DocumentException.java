package net.ech.doc;

import java.io.IOException;

/**
 * Exception type that represents a failure to resolve or parse a document.  DocumentException extends
 * {@link java.io.IOException} so that the latter may be declared as the general error type for the
 * package.
 */
public class DocumentException
	extends IOException
{
	/**
	 * Default constructor.
	 */
    public DocumentException()
    {
    }

    /**
     * Constructor.
     * @param cause    the root cause
     */
    public DocumentException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructor.
     * @param msg     a brief description of the error
     */
    public DocumentException(String msg)
    {
        super(msg);
    }

    /**
     * Constructor.
     * @param msg      a brief description of the error
     * @param cause    the root cause
     */
    public DocumentException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
