package net.ech.config;

import java.io.IOException;

/**
 * Exception type that represents a failure to load or parse a document.
 */
public class ConfigException
    extends IOException
{
    /**
	 * 
	 */
	private static final long	serialVersionUID	= -9070423860608788849L;

	/**
	 * Default constructor.
	 */
    public ConfigException()
    {
    }

    /**
     * Constructor.
     * @param cause    the root cause
     */
    public ConfigException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructor.
     * @param msg     a brief description of the error
     */
    public ConfigException(String msg)
    {
        super(msg);
    }

    /**
     * Constructor.
     * @param msg      a brief description of the error
     * @param cause    the root cause
     */
    public ConfigException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
