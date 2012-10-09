package net.ech.util;

/**
 * A base implementation of DFilter that allows all to pass through.
 */
public class AbstractDFilter
	implements DFilter
{
	/**
	 * @inheritDoc
	 */
    public boolean preallow(DQuery source)
	{
		return true;
	}

	/**
	 * @inheritDoc
	 */
    public boolean postallow(DQuery copied)
	{
		return true;
	}
}
