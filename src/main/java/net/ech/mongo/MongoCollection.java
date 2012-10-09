package net.ech.mongo;

import com.mongodb.*;
import java.io.IOException;

/**
 * A wrapper for Mongo driver classes.
 */
public interface MongoCollection
{
	public MongoURI getUri();

	public String getCollectionName();

    public void act(MongoCollectionAction action)
		throws IOException;
}
