package net.ech.util;

import java.io.IOException;
import java.util.*;

public interface DocumentCache
{
    public Object getDocument(Map<String,Object> values)
        throws IOException;

    public void putDocument(Map<String,Object> document)
        throws IOException;
}
