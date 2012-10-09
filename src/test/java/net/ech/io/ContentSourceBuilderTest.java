package net.ech.io;

import net.ech.codec.*;
import net.ech.config.*;
import net.ech.io.jesque.*;
import net.ech.io.template.*;
import net.ech.util.*;
import net.ech.mongo.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ContentSourceBuilderTest
{
	Hash config;
	Configuration configuration;
	Object result;
	ContentSource contentSourceResult;

	@Before
	public void setUp() throws Exception
	{
		result = null;
		contentSourceResult = null;
		config = new Hash();
		configuration = new Configuration(new ContentHandleRef(new JsonContentHandle(config)));
		configuration.installBuilder(new MongoPoolBuilder(configuration));
		configuration.installBuilder(new Builder<EvaluatorFactory>() {
			@Override
			public Class<EvaluatorFactory> getClientClass() { return EvaluatorFactory.class; }

			@Override 
			public EvaluatorFactory build(DQuery dq) { 
				return new StaticEvaluatorFactory(null);
			}
		});
	}

	@Test
	public void testBuildNull() throws Exception
	{
		try {
			execute(null);
		}
		catch (DocumentException e) {
			assertEquals("{test}: configuration required for net.ech.io.ContentSource", e.getMessage());
		}
	}

	@Test
	public void testBuildEmpty() throws Exception
	{
		try {
			execute(new Hash());
		}
		catch (DocumentException e) {
			assertEquals("{test}: exactly one of (url, path) must be specified", e.getMessage());
		}
	}

	@Test
	public void testBuildUrl() throws Exception
	{
		execute(new Hash("url", "http://swoop.com"));
		assertNotNull(contentSourceResult);
	}

	@Test
	public void testBuildUrlWithType() throws Exception
	{
		execute(new Hash("_type", "url").addEntry("url", "http://swoop.com"));
		assertNotNull(contentSourceResult);
	}

	@Test
	public void testNonStaticCacheAdvice() throws Exception
	{
		execute(new Hash("path", "src/test/resources/"));
		assertEquals(ContentHandle.CacheAdvice.DEFAULT, contentSourceResult.resolve(new ContentRequest("test.txt")).getCacheAdvice());
	}

	@Test
	public void testStaticCacheAdvice() throws Exception
	{
		execute(new Hash("path", "src/test/resources/").addEntry("static", true));
		assertEquals(ContentHandle.CacheAdvice.CACHE_INDEFINITELY, contentSourceResult.resolve(new ContentRequest("test.txt")).getCacheAdvice());
	}

	@Test
	public void testSetContentType() throws Exception
	{
		execute(new Hash("_type", "url").addEntry("url", "file:src/test/resources/").addEntry("contentType", "text/csv"));
		assertEquals("text/csv", contentSourceResult.resolve(new ContentRequest("test.txt")).getCodec().getContentType());
	}

	@Test
	public void testSetCharacterEncoding() throws Exception
	{
		execute(new Hash("_type", "url").addEntry("url", "file:src/test/resources/").addEntry("characterEncoding", "ISO"));
		assertEquals("ISO", contentSourceResult.resolve(new ContentRequest("test.txt")).getCodec().getCharacterEncoding());
	}

	@Test
	public void testBuildTemplate() throws Exception
	{
		execute(new Hash("_type", "template").addEntry("source", new Hash("url", "http://swoop.com")));
		assertTrue(contentSourceResult instanceof TemplateContentSource);
	}

	@Test
	public void testBuildJsonp() throws Exception
	{
		execute(new Hash("_type", "jsonp").addEntry("source", new Hash("url", "http://swoop.com")));
		assertTrue(contentSourceResult instanceof JsonpWrapperContentSource);
	}

	@Test
	public void testBuildStructured() throws Exception
	{
		execute(new Hash("_type", "structure").addEntry("structure", new Hash()));
		assertTrue(contentSourceResult instanceof StructuredContentSource);
	}

	@Test
	public void testBuildBadType() throws Exception
	{
		try {
			execute(new Hash("_type", "boogie").addEntry("path", Collections.singletonList("http://swoop.com")));
			fail("should not be reached");
		}
		catch (DocumentException e) {
			assertEquals("{test}: boogie: bad configuration type for net.ech.io.ContentSource", e.getMessage());
		}
	}

	private void execute(Object input) throws Exception
	{
		result = new ContentSourceBuilder(configuration).build(new DQuery(input, "test"));
		if (result instanceof ContentSource) {
			contentSourceResult = (ContentSource) result;
		}
	}
}
