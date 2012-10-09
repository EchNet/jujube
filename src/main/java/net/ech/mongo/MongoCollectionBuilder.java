package net.ech.mongo;

import net.ech.config.*;
import net.ech.util.*;
import java.io.*;
import java.util.*;
import com.mongodb.*;

public class MongoCollectionBuilder 
    extends AbstractBuilder<MongoCollection>
{
    private String defaultCollectionName;

	public MongoCollectionBuilder(Configuration configuration)
	{
		super(configuration);
	}

	public MongoCollectionBuilder(String defaultCollectionName, Configuration configuration)
	{
		super(configuration);
		this.defaultCollectionName = defaultCollectionName;
	}

	@Override
	public Class<MongoCollection> getClientClass()
	{
		return MongoCollection.class;
	}

	@Override
    protected MongoCollection buildByType(DQuery dq, String type)
        throws IOException
	{
		MongoPool mongoPool = getConfiguration().getBean(MongoPool.class);

		MongoURI uri = new MongoURI(dq.find("url").require(String.class));

		String collectionName = getCollectionName(uri, dq);

		return mongoPool.createCollection(uri, collectionName);
	}

	private String getCollectionName(MongoURI uri, DQuery dq)
		throws IOException
	{
		String configCollectionName = dq.find("collection").cast(String.class, null);
		if (configCollectionName != null)
			return configCollectionName;
		if (uri.getCollection() != null && !"".equals(uri.getCollection()))
			return uri.getCollection();
		if (defaultCollectionName != null) 
			return defaultCollectionName;
		return dq.find("collection").require(String.class);  // logically, this will throw a meaningful error, not return!
	}
}
