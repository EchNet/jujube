package net.ech.io;

import net.ech.config.*;
import net.ech.io.mongo.MongoContentSourceBuilder;
import net.ech.io.file.FileContentSourceBuilder;
import net.ech.io.template.*;
import net.ech.util.*;
import net.ech.mongo.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ContentSourceBuilder 
    extends AbstractBuilder<ContentSource>
{
    public ContentSourceBuilder(Configuration configuration)
	{
		super(configuration);
	}

	@Override
	public Class<ContentSource> getClientClass()
	{
		return ContentSource.class;
	}

	@Override
	protected ContentSource buildAggregate(DQuery dq)
		throws IOException
	{
		final PathContentSource pathSource = new PathContentSource();
		dq.each(new DHandler() { 
			public void handle(DQuery child) throws IOException {
				pathSource.addContentSource(build(child));
			}
		});
		return pathSource;
	}

	@Override
	protected ContentSource buildByType(DQuery dq, String type)
		throws IOException
	{
		if ("literal".equals(type)) {
			return new ContentHandleRef(new BufferedContentHandle("(literal)", ContentTypes.getDefaultCodec(dq.find("contentType").cast(String.class, ContentTypes.JSON_CONTENT_TYPE)), dq.find("data").get()));
		}
		else if ("template".equals(type)) {
			return new TemplateContentSource(build(dq.find("source")), getConfiguration().getBean(EvaluatorFactory.class));
		}
		else if ("caching".equals(type)) {
			CachingContentSource ccs = new CachingContentSource(build(dq.find("source")));
			if (!dq.find("freshness").isNull()) {
				ccs.setFreshness(dq.find("freshness").require(Integer.class));
			}
			if (!dq.find("cacheSize").isNull()) {
				ccs.setCacheSize(dq.find("cacheSize").require(Integer.class));
			}
			return ccs;
		}
		else if ("jsonp".equals(type)) {
			return new JsonpWrapperContentSource(build(dq.find("source")));
		}
		else if ("structure".equals(type)) {
			final Map<String,ContentSource> map = new HashMap<String,ContentSource>();
			DQuery struct = dq.find("structure");
			struct.require(Map.class);
			struct.each(new DHandler() { 
				public void handle(DQuery child) throws IOException {
					map.put(child.getPath().getLast().toString(), build(child));
				}
			});
			return new StructuredContentSource(map);
		}
		else if ("url".equals(type) || "file".equals(type)) {
				return new FileContentSourceBuilder(getConfiguration()).build(dq);
		}
		else if (type != null && type.startsWith("mongo")) {
			return new MongoContentSourceBuilder(getConfiguration()).build(dq);
		}
		else if (type == null) {
			String url = dq.find("url").get(String.class);
			if (url != null && url.startsWith("mongo")) {
				return new MongoContentSourceBuilder(getConfiguration()).build(dq);
			}
			else {
				return new FileContentSourceBuilder(getConfiguration()).build(dq);
			}
		}
		else {
			return super.buildByType(dq, type);
		}
	}
}
