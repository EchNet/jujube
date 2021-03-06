package net.ech.util;

import java.io.IOException;

/**
 * Exception type that represents a failure to load or parse a document.
 */
public class DocumentException
    extends IOException
{
    /**
	 * 
	 */
	private static final long	serialVersionUID	= -6749478808661719908L;

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
