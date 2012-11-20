package net.ech.mongo;

import java.util.*;

class MongoCollectionResourceStruct
{
	public final static String[] DEFAULT_PATH_FIELDS = new String[] { "_id" };

	protected MongoDatabase mongoDatabase;
	protected String collectionName;
	protected String[] pathFields = DEFAULT_PATH_FIELDS;
	protected Map<String,Object> filter;

	/**
	 * Default constructor.
	 */
	public MongoCollectionResourceStruct()
	{
	}

	/**
	 * Copy constructor.
	 */
	public MongoCollectionResourceStruct(MongoCollectionResourceStruct that)
	{
		this.mongoDatabase = that.mongoDatabase;
		this.collectionName = that.collectionName;
		this.pathFields = that.pathFields;
		this.filter = new HashMap<String,Object>(that.filter);
	}
}
