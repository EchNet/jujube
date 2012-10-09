package net.ech.service;

import java.io.IOException;

/**
 * Exception type that represents a badly formed request.
 */
public class RequestException
    extends IOException
{
    /**
     * Default constructor.
     */
    public RequestException()
    {
    }

    /**
     * Constructor.
     * @param cause    the root cause
     */
    public RequestException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructor.
     * @param msg     a brief description of the error
     */
    public RequestException(String msg)
    {
        super(msg);
    }

    /**
     * Constructor.
     * @param msg      a brief description of the error
     * @param cause    the root cause
     */
    public RequestException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
