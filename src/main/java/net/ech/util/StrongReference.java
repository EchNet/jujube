package net.ech.util;

public class StrongReference<T>
{
	private T ref;

	public StrongReference()
	{
	}

	public StrongReference(T ref)
	{
		this.ref = ref;
	}

	public void set(T ref)
	{
		this.ref = ref;
	}

	public T get()
	{
		return ref;
	}

	public void clear()
	{
		this.ref = null;
	}
}
