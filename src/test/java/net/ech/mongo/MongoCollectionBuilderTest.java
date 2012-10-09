package net.ech.mongo;

import com.mongodb.*;
import net.ech.config.*;
import net.ech.io.*;
import net.ech.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.ServletException;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MongoCollectionBuilderTest
{
	Configuration configuration;

	@Before
	public void setUp() throws Exception
	{
		configuration = new Configuration(new ContentHandleRef(new JsonContentHandle(new Hash())));
		configuration.installBuilder(new MongoPoolBuilder(configuration));
	}

    @Test
    public void testBuildReturnsMongoCollection() throws Exception
    {
		MongoCollectionBuilder mongoCollectionBuilder = new MongoCollectionBuilder("default", configuration);
		assertTrue(mongoCollectionBuilder.build(new DQuery(new Hash("url", "mongodb://foo"))) instanceof MongoCollection);
    }

    @Test
    public void testDefaultCollectionHolds() throws Exception
    {
		Hash config = new Hash()
			.addEntry("url", "mongodb://serverhost/database");
		MongoCollectionBuilder mongoCollectionBuilder = new MongoCollectionBuilder("default", configuration);
		assertEquals("default", mongoCollectionBuilder.build(new DQuery(config)).getCollectionName()); 
    }

	/***** COMMENT THIS OUT until we figure out how to specify the collection in a mongodb URI
    @Test
    public void testUriCollectionHolds() throws Exception
    {
		Hash config = new Hash()
			.addEntry("url", "mongodb://serverhost/database/collection");
		MongoCollectionBuilder mongoCollectionBuilder = new MongoCollectionBuilder(configuration);
		assertEquals("collection", mongoCollectionBuilder.build(new DQuery(config)).getCollectionName()); 
    }
	*****/

	/***** COMMENT THIS OUT until we figure out how to specify the collection in a mongodb URI
    @Test
    public void testUriCollectionOverridesDefault() throws Exception
    {
		Hash config = new Hash()
			.addEntry("url", "mongodb://serverhost/database/collection");
		MongoCollectionBuilder mongoCollectionBuilder = new MongoCollectionBuilder("default", configuration);
		assertEquals("collection", mongoCollectionBuilder.build(new DQuery(config)).getCollectionName()); 
    }
	*****/

    @Test
    public void testConfigCollectionOverridesUri() throws Exception
    {
		Hash config = new Hash()
			.addEntry("url", "mongodb://serverhost/database/collection")
			.addEntry("collection", "COLLECTION");
		MongoCollectionBuilder mongoCollectionBuilder = new MongoCollectionBuilder(configuration);
		assertEquals("COLLECTION", mongoCollectionBuilder.build(new DQuery(config)).getCollectionName()); 
    }

    @Test
    public void testConfigCollectionOverridesDefault() throws Exception
    {
		Hash config = new Hash()
			.addEntry("url", "mongodb://serverhost/database")
			.addEntry("collection", "COLLECTION");
		MongoCollectionBuilder mongoCollectionBuilder = new MongoCollectionBuilder("default", configuration);
		assertEquals("COLLECTION", mongoCollectionBuilder.build(new DQuery(config)).getCollectionName()); 
    }

    @Test
    public void testConfigCollectionRequiredIfUriLacksCollectionAndNoDefault() throws Exception
    {
		MongoCollectionBuilder mongoCollectionBuilder = new MongoCollectionBuilder(configuration);
		Hash config = new Hash()
			.addEntry("url", "mongodb://serverhost/database");
		try
		{
			mongoCollectionBuilder.build(new DQuery(config));
			fail("should not be reached");
		}
		catch (DocumentException e) 
		{
			assertEquals("collection: missing node", e.getMessage());
		}
    }

	@Test
	public void testMongoSanity1() throws Exception
	{
		MongoURI uri = new MongoURI("mongodb://devtest:shopxdevshopx@ds033017-a1.mongolab.com:33017/test");
		assertEquals("devtest", uri.getUsername());
		assertEquals("shopxdevshopx", new String(uri.getPassword()));
		assertEquals("ds033017-a1.mongolab.com:33017", uri.getHosts().get(0));
		assertEquals("test", uri.getDatabase());
	}
}
