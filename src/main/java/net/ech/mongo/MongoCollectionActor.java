package net.ech.mongo;

import java.io.IOException;

public interface MongoCollectionActor
{
	public void act(MongoCollectionAction action)
		throws IOException;
}

