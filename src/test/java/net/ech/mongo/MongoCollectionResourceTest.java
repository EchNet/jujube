package net.ech.mongo;

import com.mongodb.*;
import java.io.*;
import java.util.*;
import net.ech.nio.*;
import net.ech.nio.json.JsonCodec;
import net.ech.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class MongoCollectionResourceTest
{
	public final static String GOOD_URI = "mongodb://localhost:27027/test";

	private final static String[] DWARVES = {
		"Sleepy", "Sneezy", "Grumpy", "Dopey", "Doc", "Bashful", "Happy"
	};

	public MongoCollectionResourceTest() throws Exception
	{
		Runtime.getRuntime().exec("./test_mongo.sh");
		setUpDwarvesCollection();
	}

    @Test
    public void testToString() throws Exception
    {
		MongoCollectionResource resource = getDwarfResource();
		assertEquals(GOOD_URI + "#dwarves", resource.toString());
	}

    @Test
    public void testDwarfReaders() throws Exception
    {
		MongoCollectionResource resource = getDwarfResource();
		for (int i = 0; i < DWARVES.length; ++i) {
			ItemHandle item = resource.resolve(Query.fromUriString("//dwarf/" + i));
			Object obj = new JsonCodec().decode(item.openReader());
			assertTrue(obj instanceof Map);
			assertEquals(Integer.toString(i), ((Map<String,Object>)obj).get("id"));
			assertEquals(DWARVES[i], ((Map<String,Object>)obj).get("name"));
		}
	}

    @Test
    public void testDwarfMimeType() throws Exception
    {
		MongoCollectionResource resource = getDwarfResource();
		ItemHandle item = resource.resolve(Query.fromUriString("//dwarf/0"));
		assertNotNull(item.getMetadata());
		assertEquals("application/json", item.getMetadata().getMimeType());
	}

    @Test
    public void testDwarfCharacterEncoding() throws Exception
    {
		MongoCollectionResource resource = getDwarfResource();
		ItemHandle item = resource.resolve(Query.fromUriString("//dwarf/0"));
		assertNotNull(item.getMetadata());
		assertEquals("UTF-8", item.getMetadata().getCharacterEncoding());
	}

    @Test
    public void testDwarfCachePeriod() throws Exception
    {
		MongoCollectionResource resource = getDwarfResource();
		ItemHandle item = resource.resolve(Query.fromUriString("//dwarf/0"));
		assertNotNull(item.getMetadata());
		assertEquals(new Long(0), item.getMetadata().getCachePeriod());
	}

    @Test
    public void testDwarfInputStreams() throws Exception
    {
		MongoCollectionResource resource = getDwarfResource();
		for (int i = 0; i < DWARVES.length; ++i) {
			ItemHandle item = resource.resolve(Query.fromUriString("//dwarf/" + i));
			Object obj = new JsonCodec().decode(new InputStreamReader(item.openInputStream(), "UTF-8"));
			assertTrue(obj instanceof Map);
			assertEquals(Integer.toString(i), ((Map<String,Object>)obj).get("id"));
			assertEquals(DWARVES[i], ((Map<String,Object>)obj).get("name"));
		}
	}

    @Test
    public void testDwarfNotFound() throws Exception
    {
		MongoCollectionResource resource = getDwarfResource();
		try {
			resource.resolve(Query.fromUriString("//dwarf/100"));
			fail("should not be reached");
		}
		catch (FileNotFoundException e) {
		}
	}

    @Test
    public void testFilteredDwarf() throws Exception
    {
		MongoCollectionResource.Config resourceConfig = getDwarfResourceConfig();
		resourceConfig.setFilter(new Hash("id", 0));
		MongoCollectionResource resource = new MongoCollectionResource(resourceConfig);
		ItemHandle item = resource.resolve(Query.fromUriString("//dwarf/0"));
		Object obj = new JsonCodec().decode(new InputStreamReader(item.openInputStream(), "UTF-8"));
		assertTrue(obj instanceof Map);
		assertFalse(((Map<String,Object>)obj).containsKey("id"));
	}

	private MongoCollectionResource getDwarfResource() throws Exception
	{
		return new MongoCollectionResource(getDwarfResourceConfig());
	}

	private MongoCollectionResource.Config getDwarfResourceConfig() throws Exception
	{
		MongoCollectionResource.Config resourceConfig = new MongoCollectionResource.Config();
		resourceConfig.setMongoDatabase(new MongoDatabase(new MongoDatabase.Config(GOOD_URI)));
		resourceConfig.setCollectionName("dwarves");
		return resourceConfig;
	}

	private MongoDatabase setUpDwarvesCollection() throws Exception
	{
		MongoDatabase db = new MongoDatabase(new MongoDatabase.Config(GOOD_URI));

		db.withCollection("dwarves").act(new MongoCollectionAction() {
			@Override
			public void act(DBCollection collection) {
				collection.remove(new BasicDBObject());

				for (int i = 0; i < DWARVES.length; ++i) {
					DBObject dwarf = new BasicDBObject("id", Integer.toString(i));
					dwarf.put("name", DWARVES[i]);
					collection.save(dwarf);
				}
			}
		});

		return db;
	}
}
