package net.ech.config;

import net.ech.codec.*;
import net.ech.io.*;
import net.ech.io.file.*;
import net.ech.util.*;
import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import org.junit.*;
import org.springframework.mock.web.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class CompositeDocumentTest
{
	private String lastTempFile;
	private ContentSource configSource = new FileContentSource(new File("config"));

	private CompositeDocument createSingleFileDocument(Object data, Codec encoder)
		throws Exception
	{
		File tempFile = File.createTempFile("temp", ".json");
		tempFile.deleteOnExit();
		FileContentSource fileContentSource = new FileContentSource("/");
		fileContentSource.setCodec(new JsonCodec());
		ContentQuery tempFileQuery = new ContentSourceQuery(fileContentSource, new ContentRequest(tempFile.toString()));
		this.lastTempFile = tempFile.toString();

		Writer writer = new FileWriter(tempFile);
		encoder.encode(data, writer);
		writer.close();

		CompositeDocument comp = new CompositeDocument();
		comp.addSource(tempFileQuery);
		return comp;
	}

	@Test
	public void testEmptyYieldsNullDocument() throws Exception
	{
		assertNull(new CompositeDocument().query().getDocument());
	}

	@Test
	public void testFileVersion() throws Exception
	{
		assertNotNull(createSingleFileDocument(new Hash(), new JsonCodec()).query().getVersion());
	}

	@Test
	public void testFileVersionConsistent() throws Exception
	{
		CompositeDocument comp = createSingleFileDocument(new Hash(), new JsonCodec());
		String version1 = comp.query().getVersion();
		String version2 = comp.query().getVersion();
		assertEquals(version1, version2);
	}

	@Test
	public void testInAndOut() throws Exception
	{
		Object data = new Hash("foo", "bar");
		CompositeDocument comp = createSingleFileDocument(data, new JsonCodec());
		assertEquals(data, comp.query().getDocument());
	}

	@Test
	public void testInvalidJsonFails() throws Exception
	{
		CompositeDocument comp = createSingleFileDocument("_+_+_+_+_+_+_+_+_+_+_+_+", new TextCodec());
		try {
			comp.query();
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals(lastTempFile + ": badly formed configuration document", e.getMessage());
		}
	}

	@Test
	public void testRefreshInvalidJsonFailsRrepeatedly() throws Exception
	{
		CompositeDocument comp = createSingleFileDocument("_+_+_+_+_+_+_+_+_+_+_+_+", new TextCodec());
		for (int i = 0; i < 3; ++i) {
			try {
				comp.query();
				fail("should not be reached");
			}
			catch (IOException e) {
				// Expected.
			}
		}
	}

	@Test
	public void testFileNotFound() throws Exception
	{
		CompositeDocument comp = new CompositeDocument();
		comp.addSource(new ContentSourceQuery(new FileContentSource(new File(".")), new ContentRequest("not-there.json")));
		assertNull(comp.query().getDocument());
	}

	@Test
	public void testManyChildrenCorrectDoc() throws Exception
	{
		CompositeDocument comp = new CompositeDocument();
		comp.addSource(new ContentHandleRef(new JsonContentHandle(new Hash("foo", "bar"))));
		comp.addSource(new ContentHandleRef(new JsonContentHandle(null)));
		comp.addSource(new ContentHandleRef(new JsonContentHandle(new Hash("foo", "baz"))));
		assertEquals(new Hash("foo", "baz"), comp.query().getDocument());
	}

	@Test
	public void testMergingOfMaps() throws Exception
	{
		CompositeDocument comp = new CompositeDocument();
		comp.addSource(new ContentHandleRef(new JsonContentHandle(new Hash("foo", new Hash("a", "b")))));
		comp.addSource(new ContentHandleRef(new JsonContentHandle(null)));
		comp.addSource(new ContentHandleRef(new JsonContentHandle(new Hash("foo", new Hash("c", "d")))));
		assertEquals(new Hash("foo", new Hash("a", "b").addEntry("c", "d")), comp.query().getDocument());
	}

	@Test
	public void testMergingOfMapsWithOverride() throws Exception
	{
		CompositeDocument comp = new CompositeDocument();
		comp.addSource(new ContentHandleRef(new JsonContentHandle(new Hash("foo", new Hash("a", "b")))));
		comp.addSource(new ContentHandleRef(new JsonContentHandle(new Hash("foo", new Hash("a", "d")))));
		assertEquals(new Hash("foo", new Hash("a", "d")), comp.query().getDocument());
	}

	@Test
	public void testNullInTheMiddle() throws Exception
	{
		CompositeDocument comp = new CompositeDocument();
		comp.addSource(new ContentHandleRef(new JsonContentHandle(new Hash("foo", new Hash("a", "b")))));
		comp.addSource(new ContentHandleRef(new JsonContentHandle(null)));
		comp.addSource(new ContentHandleRef(new JsonContentHandle(new Hash("foo", new Hash("a", "d")))));
		assertEquals(new Hash("foo", new Hash("a", "d")), comp.query().getDocument());
	}

	@Test
	public void testNullOnTheRight() throws Exception
	{
		CompositeDocument comp = new CompositeDocument();
		comp.addSource(new ContentHandleRef(new JsonContentHandle(new Hash("foo", new Hash("a", "b")))));
		comp.addSource(new ContentHandleRef(new JsonContentHandle(new Hash("foo", new Hash("a", "d")))));
		comp.addSource(new ContentHandleRef(new JsonContentHandle(null)));
		assertEquals(new Hash("foo", new Hash("a", "d")), comp.query().getDocument());
	}

	@Test
	public void testNonRefreshedString() throws Exception
	{
		Hash configDoc = new Hash("abc", "123");
		ContentQuery configDocSource = new ContentHandleRef(new JsonContentHandle(configDoc));
		CompositeDocument comp = new CompositeDocument();
		comp.addSource(configDocSource);
		comp.query();
		configDoc.put("abc", "456");
		assertEquals("123", new DQuery(comp.query().getDocument()).find("abc").get());
	}

	@Test
	public void testRefreshedString() throws Exception
	{
		Hash configDoc = new Hash("abc", "123");
		ContentQuery configDocSource = new ContentHandleRef(new JsonContentHandle(configDoc));
		CompositeDocument comp = new CompositeDocument();
		comp.setFreshnessPeriod(-1);  // !!!
		comp.addSource(configDocSource);
		comp.query();
		configDoc.put("abc", "456");
		assertEquals("456", new DQuery(comp.query().getDocument()).find("abc").get());
	}

	@Test
	public void testFileVersionNotNull() throws Exception
	{
		CompositeDocument comp = new CompositeDocument();
		comp.addSource(new ContentSourceQuery(configSource, new ContentRequest("common.json")));
		assertNotNull(comp.query().getVersion());
	}

	// Acid test for config document caching.
	@Test
	public void testCachedSingleFileConfigNeedsNoRefresh() throws Exception
	{
		final int PAUSE = 10;
		CompositeDocument comp = new CompositeDocument();
		comp.setFreshnessPeriod(0);
		comp.addSource(new ContentSourceQuery(configSource, new ContentRequest("common.json")));
		String v1 = comp.query().getVersion();
		Thread.sleep(PAUSE);
		String v2 = comp.query().getVersion();
		assertEquals(v1, v2);
	}

	// Acid test for config document caching.
	@Test
	public void testCachedMultiFileConfigNeedsNoRefresh() throws Exception
	{
		final int PAUSE = 10;
		CompositeDocument comp = new CompositeDocument();
		comp.setFreshnessPeriod(0);
		comp.addSource(new ContentSourceQuery(configSource, new ContentRequest("common.json")));
		comp.addSource(new ContentSourceQuery(configSource, new ContentRequest("dev.json")));
		String v1 = comp.query().getVersion();
		Thread.sleep(PAUSE);
		String v2 = comp.query().getVersion();
		assertEquals(v1, v2);
	}
}
