package net.ech.mongo;

import com.mongodb.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class MongoDatabaseTest
{
	public final static String GOOD_URI = "mongodb://localhost:27027/test";

	public MongoDatabaseTest() throws Exception
	{
		Runtime.getRuntime().exec("./test_mongo.sh");
	}

    @Test
    public void testConstructMinimal() throws Exception
    {
		MongoDatabase.Config config = new MongoDatabase.Config();
		config.setUri("mongodb://localhost/test");
		MongoDatabase db = new MongoDatabase(config);
		assertEquals("mongodb://localhost/test", db.toString());
	}

    @Test
    public void testBadHostName() throws Exception
    {
		MongoDatabase db = new MongoDatabase(new MongoDatabase.Config("mongodb://nosuch/test"));
		try {
			db.withCollection("test").act(null);
			fail("should not be reached");
		}
		catch (java.net.UnknownHostException e) {
			assertEquals("nosuch", e.getMessage());
		}
	}

    @Test
    public void testToStringOmitsQueryParams() throws Exception
    {
		MongoDatabase.Config config = new MongoDatabase.Config();
		config.setUri(GOOD_URI + "?connectTimeoutMS=100");
		MongoDatabase db = new MongoDatabase(config);
		assertEquals(GOOD_URI, db.toString());
	}

    @Test
    public void testMongoOptionsMakeItThru() throws Exception
    {
		MongoDatabase.Config config = new MongoDatabase.Config(GOOD_URI + "?connectTimeoutMS=100");
		MongoDatabase db = new MongoDatabase(config);
		db.withCollection("test").act(new MongoCollectionAction() {
			@Override
			public void act(DBCollection collection) {
				assertEquals(100, collection.getDB().getMongo().getMongoOptions().getConnectTimeout());
			}
		});
	}

	@Test
	public void testConstructNoDB() throws Exception
	{
		try {
			new MongoDatabase(new MongoDatabase.Config("mongodb://localhost"));
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("no DB specified", e.getMessage());
		}
	}

	@Test
	public void testStaysOpen() throws Exception
	{
		MongoDatabase.Config config = new MongoDatabase.Config(GOOD_URI);
		MongoDatabase db = new MongoDatabase(config);
		assertFalse(db.isOpen());
		db.withCollection("pouf").act(new MongoCollectionAction() {
			@Override
			public void act(DBCollection collection) {
				collection.remove(new BasicDBObject());
			}
		});
		assertTrue(db.isOpen());
		db.withCollection("pouf").act(new MongoCollectionAction() {
			@Override
			public void act(DBCollection collection) {
				collection.save(new BasicDBObject("msg", "hello"));
			}
		});
		assertTrue(db.isOpen());
		db.withCollection("pouf").act(new MongoCollectionAction() {
			@Override
			public void act(DBCollection collection) {
				DBObject obj = collection.findOne(new BasicDBObject());
				assertNotNull(obj);
				assertEquals("hello", obj.get("msg"));
			}
		});
		assertTrue(db.isOpen());
	}

	@Test
	public void testGetsClosed() throws Exception
	{
		MongoDatabase.Config config = new MongoDatabase.Config(GOOD_URI);
		config.setKeepAliveMillis(1);
		final MongoDatabase db = new MongoDatabase(config);
		db.withCollection("pouf").act(new MongoCollectionAction() {
			@Override
			public void act(DBCollection collection) throws IOException {
				db.withCollection("pouf").act(new MongoCollectionAction() {
					@Override
					public void act(DBCollection collection) throws IOException {
						db.withCollection("pouf").act(new MongoCollectionAction() {
							@Override
							public void act(DBCollection collection) throws IOException {
							}
						});
						db.withCollection("pouf").act(new MongoCollectionAction() {
							@Override
							public void act(DBCollection collection) throws IOException {
							}
						});
					}
				});
			}
		});
		Thread.sleep(100);
		assertFalse(db.isOpen());
	}
}
