package net.ech.util;

public class ObjectRef
{
	private Object obj;

	public ObjectRef()
	{
	}

	public ObjectRef(Object obj)
	{
		this.obj = obj;
	}

	public Object getObject()
	{
		return obj;
	}

	public void setObject(Object obj)
	{
		this.obj = obj;
	}
}
