package net.ech.io;

import net.ech.util.*;
import java.io.IOException;
import java.util.*;

public class MetaWrapperContentSource
	extends ProxyContentSource
	implements ContentSource
{
	public MetaWrapperContentSource(ContentSource inner)
	{
		super(inner);
	}

	@Override
    public ContentHandle resolve(final ContentRequest request)
        throws IOException
	{
		ContentHandle innerContent = super.resolve(request);

		return new JsonContentHandle(innerContent.getSource(), innerContent.getDocument())
		{
			@Override
			public Object getDocument()
				throws IOException
			{
				Hash hash = new Hash();
				if (request.getPath() != null && !request.getPath().equals("")) {
					hash.addEntry("path", request.getPath());
				}
				if (request.getParameters() != null && request.getParameters().size() > 0) {
					hash.addEntry("parameters", request.getParameters());
				}
				String source = getSource();
				if (source != null && !source.equals("")) {
					hash.addEntry("source", source);
				}
				Object document = super.getDocument();
				if (document != null) {
					hash.addEntry("data", document);
				}
				return hash;
			}
		};
	}
}
