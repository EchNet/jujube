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
		if (addPathFields(q, query.getPath()) != null) {
			throw new IOException(query + ": does not resolve to a single item");
		}
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

	private String addPathFields(DBObject q, String path)
		throws IOException
	{
		int pcx = 0;

		if (path != null) {
			String[] pathComps = path.split("\\/");

			for (; pcx < pathComps.length; ++pcx) {
				if (pcx >= pathFields.length) {
					throw new IOException(path + ": too many path components");
				}
				q.put(pathFields[pcx], pathComps[pcx]);
			}
		}

		return pcx < pathFields.length ? pathFields[pcx] : null;
	}
}
