package net.ech.util;

public interface DFilter
{
	/**
	 * On initial visit to a source node, determine whether the source might be worth
	 * including in the copy.
	 */
    public boolean preallow(DQuery source);

	/**
	 * After a node has been copied, exercise veto privilege.
	 */
    public boolean postallow(DQuery copied);
}
