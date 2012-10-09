package net.ech.io.file;

import net.ech.codec.*;
import net.ech.io.*;
import net.ech.util.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UrlContentSourceTest
{
	@Test
	public void testEmptyPath() throws Exception
	{
		assertUrlResolution("file:src/test/resources/test.txt", new URL("file:src/test/resources/test.txt"), new ContentRequest(""));
    }

	@Test
	public void testExtensionApplied() throws Exception
	{
		UrlContentSource contentSource = new UrlContentSource(new URL("file:src/test/resources/test"));
		contentSource.setExtension(".txt");
		assertUrlResolution("file:src/test/resources/test.txt", contentSource, new ContentRequest(""));
    }

	@Test
	public void testExtensionReplaced() throws Exception
	{
		UrlContentSource contentSource = new UrlContentSource(new URL("file:src/test/resources/test.html"));
		contentSource.setExtension(".txt");
		contentSource.setStripExtension(true);
		assertUrlResolution("file:src/test/resources/test.txt", contentSource, new ContentRequest(""));
    }

	@Test
	public void testNonEmptyPath() throws Exception
	{
		assertUrlResolution("file:src/test/resources/test.txt", new URL("file:src/test/resources/foo"), new ContentRequest("test.txt"));
    }

	@Test
	public void testThatAllImportantSlash() throws Exception
	{
		assertUrlResolution("file:src/test/resources/test.txt", new URL("file:src/test/resources/"), new ContentRequest("test.txt"));
    }

	@Test
	public void testNoPath() throws Exception
	{
		assertUrlResolution("file:src/test/resources/", new URL("file:src/test/resources/"), new ContentRequest(""));
    }

	@Test
	public void testNoPathAndBaseHasNoSlash() throws Exception
	{
		assertUrlResolution("file:pom.xml", new URL("file:pom.xml"), new ContentRequest(""));
    }

	@Test
	public void testNoPathButParams() throws Exception
	{
		assertUrlResolution("file:src/test/resources/test.txt?hey=you", new URL("file:src/test/resources/test.txt"), new ContentRequest("", new Hash("hey", "you")));
    }

	@Test
	public void testPathAndParams() throws Exception
	{
		assertUrlResolution("file:src/test/resources/test.txt?hey=you", new URL("file:src/test/resources/"), new ContentRequest("test.txt", new Hash("hey", "you")));
    }

	@Test
	public void testBaseParamsNoPathButParams() throws Exception
	{
		assertUrlResolution("file:src/test/resources/test.txt?what=up&hey=you", new URL("file:src/test/resources/test.txt?what=up"), new ContentRequest("", new Hash("hey", "you")));
    }

	@Test
	public void testBaseParamsPathAndParams() throws Exception
	{
		assertUrlResolution("file:src/test/resources/test.txt?what=up&q=1&hey=you", new URL("file:src/test/resources/?what=up&q=1"), new ContentRequest("test.txt", new Hash("hey", "you")));
    }

	@Test
	public void testParamsEverywhere() throws Exception
	{
		assertUrlResolution("file:src/test/resources/test.txt?what=up&q=1&hey=you", new URL("file:src/test/resources/?what=up"), new ContentRequest("test.txt?q=1", new Hash("hey", "you")));
    }

	@Test
	public void testSetCodecType() throws Exception
	{
		UrlContentSource upcs = new UrlContentSource(new URL("file:src/test/resources/"));
		upcs.setCodec(new CsvCodec("ISO"));
		assertTrue(upcs.resolve(new ContentRequest("test.txt")).getCodec() instanceof CsvCodec);
	}

	@Test
	public void testSetCodecContentType() throws Exception
	{
		UrlContentSource upcs = new UrlContentSource(new URL("file:src/test/resources/"));
		upcs.setCodec(new CsvCodec("ISO"));
		assertEquals("text/csv", upcs.resolve(new ContentRequest("test.txt")).getCodec().getContentType());
	}

	@Test
	public void testSetCodecCharEncoding() throws Exception
	{
		UrlContentSource upcs = new UrlContentSource(new URL("file:src/test/resources/"));
		upcs.setCodec(new CsvCodec("ISO"));
		assertEquals("ISO", upcs.resolve(new ContentRequest("test.txt")).getCodec().getCharacterEncoding());
	}

	@Test
	public void testDefaultCacheAdvice() throws Exception
	{
		ContentHandle fch = new UrlContentSource(new URL("file:src/test/resources/")).resolve(new ContentRequest("test.txt"));
		assertEquals(ContentHandle.CacheAdvice.DEFAULT, fch.getCacheAdvice());
	}

	@Test
	public void testDefaultCacheAdvice2() throws Exception
	{
		ContentHandle fch = new UrlContentSource(new URL("file:src/test/resources/"), false).resolve(new ContentRequest("test.txt"));
		assertEquals(ContentHandle.CacheAdvice.DEFAULT, fch.getCacheAdvice());
	}

	@Test
	public void testSetStaticSourceSetsIndefiniteCache() throws Exception
	{
		ContentHandle fch = new UrlContentSource(new URL("file:src/test/resources/"), true).resolve(new ContentRequest("test.txt"));
		assertEquals(ContentHandle.CacheAdvice.CACHE_INDEFINITELY, fch.getCacheAdvice());
	}

	@Test
	public void testSetStaticSourceSetsIndefiniteCache2() throws Exception
	{
		UrlContentSource fcs = new UrlContentSource(new URL("file:src/test/resources/"));
		fcs.setStatic(true);
		ContentHandle fch = fcs.resolve(new ContentRequest("test.txt"));
		assertEquals(ContentHandle.CacheAdvice.CACHE_INDEFINITELY, fch.getCacheAdvice());
	}

	private void assertUrlResolution(String expected, URL baseUrl, ContentRequest contentRequest)
		throws Exception
	{
		assertUrlResolution(expected, new UrlContentSource(baseUrl), contentRequest);
	}

	private void assertUrlResolution(String expected, UrlContentSource urlContentSource, ContentRequest contentRequest)
		throws Exception
	{
		assertEquals(expected, resolvedUrlString(urlContentSource, contentRequest));
	}

	private String resolvedUrlString(UrlContentSource urlContentSource, ContentRequest contentRequest)
		throws Exception
	{
		return resolve(urlContentSource, contentRequest).getUrl().toString();
	}

	private UrlContentHandle resolve(UrlContentSource urlContentSource, ContentRequest contentRequest)
		throws Exception
	{
		return (UrlContentHandle) urlContentSource.resolve(contentRequest);
	}
}
