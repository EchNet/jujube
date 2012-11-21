package net.ech.mongo;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MongoCollectionResourceTest
{
    @Test
    public void testInitial() throws Exception
    {
		MongoDatabase.Config config = new MongoDatabase.Config();
		config.setUri("mongodb://localhost/test");
		MongoDatabase db = new MongoDatabase(config);
		MongoCollectionResource.Config resourceConfig = new MongoCollectionResource.Config();
		resourceConfig.setMongoDatabase(db);
		resourceConfig.setCollectionName("test");
		MongoCollectionResource resource = new MongoCollectionResource(resourceConfig);
	}
}
