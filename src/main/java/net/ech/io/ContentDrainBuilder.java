package net.ech.io;

import net.ech.config.*;
import net.ech.io.file.FileContentDrain;
import net.ech.io.jesque.JesqueContentDrainBuilder;
import net.ech.util.*;
import java.io.IOException;
import java.util.*;

public class ContentDrainBuilder 
    extends AbstractBuilder<ContentDrain>
{
    public ContentDrainBuilder(Configuration configuration)
    {
        super(configuration);
    }

	@Override
	public Class<ContentDrain> getClientClass()
	{
		return ContentDrain.class;
	}

	@Override
	protected ContentDrain buildFromNull(DQuery dq)
	{
		// The null drain...
		return new AbstractContentDrain() {};
	}

	@Override
    protected ContentDrain buildByType(DQuery dq, String type)
        throws IOException
    {
		if ("jesque".equals(type)) {
			return new JesqueContentDrainBuilder(getConfiguration()).build(dq);
		}
		else if ("echo".equals(type)) {
			return new EchoContentDrain();
		}
		else if ("null".equals(type)) {
			return new AbstractContentDrain() {};
		}
		else if ("file".equals(type)) {
			return new FileContentDrain(dq.find("path").require(String.class));
		}
		else {
			return super.buildByType(dq, type);
		}
	}
}
