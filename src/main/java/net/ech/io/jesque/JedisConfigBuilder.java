package net.ech.io.jesque;

import net.ech.config.*;
import net.ech.util.*;
import java.io.IOException;

public class JedisConfigBuilder 
    extends AbstractBuilder<JedisConfig>
{
    public JedisConfigBuilder(Configuration configuration)
    {
        super(configuration);
    }

	@Override
	public Class<JedisConfig> getClientClass()
	{
		return JedisConfig.class;
	}

	@Override
    protected JedisConfig buildByType(DQuery dq, String type)
        throws IOException
    {
		JedisConfig config = new JedisConfig();
		config.setHost(dq.find("host").cast(String.class, config.getHost()));
		config.setPort(dq.find("port").cast(Integer.class, config.getPort()));
		config.setTimeout(dq.find("timeout").cast(Integer.class, config.getTimeout()));
		config.setPassword(dq.find("password").cast(String.class, config.getPassword()));
		config.setNamespace(dq.find("namespace").cast(String.class, config.getNamespace()));
		config.setDatabase(dq.find("database").cast(Integer.class, config.getDatabase()));
		return config;
	}
}
