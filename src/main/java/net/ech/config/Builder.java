package net.ech.config;

import net.ech.util.DQuery;
import java.io.IOException;

public interface Builder<T> 
{
	public Class<T> getClientClass();

    public T build(DQuery dq)
        throws IOException;
}
