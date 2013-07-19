package net.ech.util;

/**
 * Exception type that represents an error in coercing an object to
 * a specific type, for assignment.
 */
public class TypeMismatchException
    extends RuntimeException
{
    /**
	 * 
	 */
	private static final long	serialVersionUID	= 2167794058124006818L;

	/**
     * Constructor.
	 * @param lhsClass the type of the assignment
	 * @param rhsValue the value being assigned
     */
    public TypeMismatchException(Class<?> lhsClass, Object rhsValue)
    {
		this(lhsClass, rhsValue, null);
    }

    /**
     * Constructor.
	 * @param lhsClass the type of the assignment
	 * @param rhsValue the value being assigned
     * @param cause    the root cause
     */
    public TypeMismatchException(Class<?> lhsClass, Object rhsValue, Throwable cause)
    {
        super("cannot coerce " + rhsValue + " to type " + lhsClass.getName(), cause);
    }
}
