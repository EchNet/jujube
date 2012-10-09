package net.ech.io.jesque;

import net.ech.config.*;
import net.ech.io.*;
import net.ech.util.*;
import java.io.IOException;

public class JesqueContentDrainBuilder 
    extends AbstractBuilder<ContentDrain>
{
    public JesqueContentDrainBuilder(Configuration configuration)
    {
        super(configuration);
    }

	@Override
	public Class<ContentDrain> getClientClass()
	{
		return ContentDrain.class;
	}

	@Override
    protected ContentDrain buildByType(DQuery dq, String type)
        throws IOException
    {
		return new JesqueContentDrain(
				new JedisConfigBuilder(getConfiguration()).build(dq.find("redis")),
				dq.find("queueName").require(String.class),
				dq.find("jobName").require(String.class));
    }
}
