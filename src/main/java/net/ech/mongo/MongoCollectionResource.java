package net.ech.mongo;

import com.mongodb.*;
import net.ech.nio.*;
import net.ech.nio.json.JsonCodec;
import net.ech.util.*;
import java.io.*;
import java.util.*;

/**
 * Implementation of Resource based on querying a specific MongoDB collection.
 */
public class MongoCollectionResource
	extends MongoCollectionResourceStruct
	implements Resource
{
	public static class Config
		extends MongoCollectionResourceStruct
	{
		public void setMongoDatabase(MongoDatabase mongoDatabase)
		{
			this.mongoDatabase = mongoDatabase;
		}

		public void setCollectionName(String collectionName)
		{
			this.collectionName = collectionName;
		}

		public void setPathFields(String[] pathFields)
		{
			this.pathFields = pathFields;
		}

		public void setFilter(Map<String,Object> filter)
		{
			this.filter = new HashMap<String,Object>(filter);
		}
	}

	/**
	 * Constructor.
	 * @param config  configuration
	 */
	public MongoCollectionResource(Config config)
	{
		super(config);
	}

	@Override
	public ItemHandle resolve(final Query query)
		throws IOException
	{
		// Compose the query.
		final DBObject q = new BasicDBObject();
		addPathFields(q, query);
		for (String paramKey : query.getParameterKeys()) {
			q.put(paramKey, query.getParameter(paramKey));  // single parameter values only
		}

		// Compose key set (optional)
		final DBObject keys = populateDBObject(new BasicDBObject("_id", 0), filter == null ? new BasicDBObject() : filter);

		// A basket to catch the result.
		final StrongReference<Object> objRef = new StrongReference<Object>();

		mongoDatabase.withCollection(collectionName).act(new MongoCollectionAction()
		{
			@Override
			public void act(DBCollection dbc)
				throws IOException, MongoException
			{
				// Execute query.
				objRef.set(dbc.findOne(q, keys));
			}
		});

		if (objRef.get() == null) {
			throw new FileNotFoundException(query.toString());
		}

		return new ItemHandle() {

			@Override
			public InputStream openInputStream()
				throws IOException
			{
				return new ByteArrayInputStream(getJsonString().getBytes("UTF-8"));
			}

			@Override
			public Reader openReader()
				throws IOException
			{
				return new StringReader(getJsonString());
			}

			@Override
			public Metadata getMetadata()
			{
				return new AbstractMetadata() {
					@Override
					public String getMimeType()
					{
						return MimeType.JSON;
					}

					@Override
					public String getCharacterEncoding()
					{
						return "UTF-8";
					}

					@Override
					public Long getCachePeriod()
					{
						return new Long(0);
					}
				};
			}

			@Override
			public String toString()
			{
				return MongoCollectionResource.this.toString() + "(" + query + ")";
			}

			private String getJsonString()
				throws IOException
			{
				return new JsonCodec().encode(objRef.get());
			}
		};
	}

	@Override
	public String toString()
	{
		return mongoDatabase + "#" + collectionName;
	}

	private DBObject populateDBObject(DBObject dbObj, Map<String,Object> source)
	{
		for (Map.Entry<String,Object> entry : source.entrySet()) {
			dbObj.put(entry.getKey(), entry.getValue());
		}
		return dbObj;
	}

	private void addPathFields(DBObject q, Query query)
		throws IOException
	{
		String path = query.getPath();
		String[] pathComps = path.equals("") ? new String[0] : path.split("\\/");

		if (pathComps.length != pathFields.length) {
			throw new IOException(query + (pathComps.length > pathFields.length ? ": too many path components" : ": does not resolve to single item"));
		}

		for (int pcx = 0; pcx < pathComps.length; ++pcx) {
			q.put(pathFields[pcx], pathComps[pcx]);
		}
	}
}
