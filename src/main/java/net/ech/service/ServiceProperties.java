package net.ech.service;

import net.ech.util.*;
import java.io.*;
import java.util.*;

public class ServiceProperties
    extends Properties
{
    private static ServiceProperties instance;

    public static ServiceProperties getInstance()
    {
        if (instance == null) {
            instance = new ServiceProperties();
        }
        return instance;
    }

    private ServiceProperties()
    {
        loadResource("maven.properties");
        loadResource("git.properties");
    }

	/**
	 * Where to find the server config file(s).  Essential to the operation of the server!
	 */
    public String getConfigFilePath()
    {
        return System.getProperty("hub.config");
    }

	/**
	 * Version string.
	 */
    public String getSourceCommitId()
    {
        return getProperty("hub.commit.id");
    }
    
	/**
	 * Version string.
	 */
    public String getSourceBranch()
    {
        return getProperty("hub.branch");
    }

	/**
	 * Version string.
	 */
    public String getSourceCommitTime()
    {
        return getProperty("hub.commit.time");
    }

    private void loadResource(String path)
    {
        InputStream in = null;
        try {
            in = ServiceProperties.class.getClassLoader().getResourceAsStream(path);
            load(in);
        }
        catch (IOException e) {
        }
        finally {
            if (in != null) {
                try { in.close(); } catch (IOException e) {}
            }
        }
    }
}
