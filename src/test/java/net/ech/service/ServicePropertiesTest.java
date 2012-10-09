package net.ech.service;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ServicePropertiesTest
{
    @Test
    public void testNotNullInstance() throws Exception
    {
        assertNotNull(ServiceProperties.getInstance());
    }

    @Test
    public void testSingleton() throws Exception
    {
        assertEquals(ServiceProperties.getInstance(), ServiceProperties.getInstance());
    }

    @Test
    public void testCommitId() throws Exception
    {
        assertNotNull(ServiceProperties.getInstance().getSourceCommitId());
    }

    @Test
    public void testBranch() throws Exception
    {
        assertNotNull(ServiceProperties.getInstance().getSourceBranch());
    }

    @Test
    public void testCommitTime() throws Exception
    {
        assertNotNull(ServiceProperties.getInstance().getSourceCommitTime());
    }
}
