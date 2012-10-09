package net.ech.mongo;

import com.mongodb.*;
import java.io.IOException;

/**
 * A function pointer, when it comes right down to it.
 */
public interface MongoCollectionAction
{
	public void act(DBCollection dbCollection)
		throws IOException, MongoException;
}
