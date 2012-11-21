package net.ech.mongo;

import com.mongodb.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MongoDatabaseTest
{
    @Test
    public void testConstructMinimal() throws Exception
    {
		MongoDatabase.Config config = new MongoDatabase.Config();
		config.setUri("mongodb://localhost/test");
		MongoDatabase db = new MongoDatabase(config);
		assertEquals("mongodb://localhost/test", db.toString());
	}

    @Test
    public void testFailedConnection() throws Exception
    {
		MongoDatabase.Config config = new MongoDatabase.Config();
		config.setUri("mongodb://nosuch:12/test");
		MongoDatabase db = new MongoDatabase(config);
		try {
			db.withCollection("test").act(new MongoCollectionAction() {
				public void act(DBCollection collection) {}
			});
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("", e.getMessage());
		}
	}

    @Test
    public void testFailedAuthentication() throws Exception
    {
		MongoDatabase.Config config = new MongoDatabase.Config();
		config.setUri("mongodb://ech1:foyfoy@localhost:12/test");
		MongoDatabase db = new MongoDatabase(config);
		try {
			db.withCollection("test").act(new MongoCollectionAction() {
				public void act(DBCollection collection) {}
			});
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("", e.getMessage());
		}
	}

    @Test
    public void testConstructAutoConnectRetry() throws Exception
    {
		MongoDatabase.Config config = new MongoDatabase.Config();
		config.setUri("mongodb://localhost/test");
		config.setAutoConnectRetry(true);
		MongoDatabase db = new MongoDatabase(config);
		assertEquals("mongodb://localhost/test", db.toString());
		db.withCollection("test").act(new MongoCollectionAction() {
			@Override
			public void act(DBCollection collection) {
				assertTrue(collection.getDB().getMongo().getMongoOptions().isAutoConnectRetry());
			}
		});
	}

	@Test
	public void testConstructNoDB() throws Exception
	{
		MongoDatabase.Config config = new MongoDatabase.Config();
		config.setUri("mongodb://localhost");
		try {
			new MongoDatabase(config);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("no DB specified", e.getMessage());
		}
	}
}
