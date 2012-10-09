package net.ech.mongo;

import com.mongodb.*;
import java.io.IOException;
import java.util.*;

/**
 * Manager of MongoCollection objects.
 */
public class MongoPool
{
	// Each Mongo object maintains a connection pool and is internally thread safe, so it's 
	// desirable to have only one per URI.
	private Map<String,MongoWrapper> pool = new HashMap<String,MongoWrapper>();

	/**
	 * Get a Mongo collection wrapper.
	 */
	public MongoCollection createCollection(String uriString)
	{
		MongoURI uri = new MongoURI(uriString);
		return createCollection(uri, uri.getCollection());
	}

	/**
	 * Get a Mongo collection wrapper.
	 */
	public MongoCollection createCollection(final MongoURI uri, final String collectionName)
	{
		if (!pool.containsKey(uri.toString())) {
			synchronized (pool) {
				if (!pool.containsKey(uri.toString())) {
					pool.put(uri.toString(), new MongoWrapper(uri));
				}
			}
		}

		final MongoWrapper mongoWrapper = pool.get(uri.toString());

		return new MongoCollection() 
		{
			@Override
			public MongoURI getUri()
			{
				return uri;
			}

			@Override
			public String getCollectionName()
			{
				return collectionName;
			}

			@Override
			public void act(MongoCollectionAction action)
				throws IOException
			{
				DBCollection dbc = mongoWrapper.use().getCollection(collectionName);
				try {
					action.act(dbc);
				}
				catch (MongoException e) {
					throw new IOException(e);
				}
				finally {
					mongoWrapper.release();
				}
			}

			/**
			 * WATCH OUT!  The value returned from this function can end up in user-visible error messages.
			 * Do not include the database authentication info!!
			 */
			public String toString()
			{
				return "mongodb::" + collectionName;
			}
		};
	}
}
