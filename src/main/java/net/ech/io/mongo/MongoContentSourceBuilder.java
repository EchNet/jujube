package net.ech.io.mongo;

import net.ech.config.*;
import net.ech.io.*;
import net.ech.util.*;
import net.ech.mongo.*;
import java.io.*;
import java.util.*;

public class MongoContentSourceBuilder 
    extends AbstractBuilder<ContentSource>
{
	MongoCollectionBuilder mongoCollectionBuilder;

    public MongoContentSourceBuilder(Configuration configuration)
	{
		super(configuration);
		mongoCollectionBuilder = new MongoCollectionBuilder(configuration);
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
		MongoCollection mongoCollection = mongoCollectionBuilder.build(dq);
		MongoQueryContentSource mSource = new MongoQueryContentSource(mongoCollection);
		if (!dq.find("pathFields").isNull()) {
			mSource.setPathFields(((List<String>) dq.find("pathFields").require(List.class)).toArray(new String[0]));
		}
		if (!dq.find("listingFields").isNull()) {
			mSource.setListingFields(((List<String>) dq.find("listingFields").require(List.class)).toArray(new String[0]));
		}
		return mSource;
	}
}
