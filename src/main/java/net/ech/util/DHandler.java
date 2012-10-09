package net.ech.util;

import java.io.IOException;

public interface DHandler
{
    public void handle(DQuery dq) throws IOException;
}
