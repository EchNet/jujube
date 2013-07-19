package net.ech.config;

import java.io.IOException;

/**
 * Internal exception type, intended to be caught and handled by the Configurator.
 */
class InternalConfigException
	extends IOException
{
    /**
	 * 
	 */
	private static final long	serialVersionUID	= 6898515116524653423L;

	/**
	 * Default constructor.
	 */
    public InternalConfigException()
    {
    }

    /**
     * Constructor.
     * @param cause    the root cause
     */
    public InternalConfigException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructor.
     * @param msg     a brief description of the error
     */
    public InternalConfigException(String msg)
    {
        super(msg);
    }

    /**
     * Constructor.
     * @param msg      a brief description of the error
     * @param cause    the root cause
     */
    public InternalConfigException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
