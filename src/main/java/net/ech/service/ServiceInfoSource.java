package net.ech.service;

import net.ech.config.*;
import net.ech.io.*;
import net.ech.util.*;
import java.io.IOException;

public class ServiceInfoSource 
    extends AbstractBuilder<ContentSource>
{
    public ServiceInfoSource(Configuration configuration)
    {
        super(configuration);
    }

	@Override
	public Class<ContentSource> getClientClass()
	{
		return ContentSource.class;
	}

	@Override
    protected ContentSource buildByType(DQuery dq, String type)
        throws IOException
    {
		return new AbstractContentSource() {
			@Override
			public ContentHandle resolve(ContentRequest request)
				throws IOException
			{
				ServiceProperties properties = ServiceProperties.getInstance();
				Configuration configuration = getConfiguration();

				return new JsonContentHandle(new Hash()
					.addEntry("hub", new Hash()
						.addEntry("version", properties.getSourceCommitId())
						.addEntry("buildTime", properties.getSourceCommitTime())
						.addEntry("mode", configuration.getString("mode", "???"))));
			}
		};
	}
}
