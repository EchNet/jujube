package net.ech.util;

/**
 * Exception type that represents an error in the process of accessing a
 * bean's properties.  Unfortunately, it's a RuntimeException.  Why?  
 * So that Map methods may throw it.
 */
public class BeanException
    extends RuntimeException
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5672176393886471595L;
	private Class<?>	beanClass;

    /**
     * Constructor.
	 * @param beanClass the related class
     */
    public BeanException(Class<?> beanClass)
    {
		super(beanClass.toString());
		this.beanClass = beanClass;
    }

    /**
     * Constructor.
	 * @param beanClass the related class
     * @param cause    the root cause
     */
    public BeanException(Class<?> beanClass, Throwable cause)
    {
        super(beanClass + ": " + cause.getMessage(), cause);
		this.beanClass = beanClass;
    }

    /**
     * Constructor.
	 * @param beanClass the related class
     * @param msg     a brief description of the error
     */
    public BeanException(Class<?> beanClass, String msg)
    {
        super(msg);
		this.beanClass = beanClass;
    }

    /**
     * Constructor.
	 * @param beanClass the related class
     * @param msg      a brief description of the error
     * @param cause    the root cause
     */
    public BeanException(Class<?> beanClass, String msg, Throwable cause)
    {
        super(msg, cause);
		this.beanClass = beanClass;
    }

	/**
	 * The related bean class.
	 */
	public Class<?> getBeanClass()
	{
		return beanClass;
	}
}
