package net.ech.nio;

import net.ech.util.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CachedResourceTest
{
	List<String> log;

	@Before
	public void setUp() throws Exception
	{
		log = new ArrayList<String>();
	}

	@Test
	public void testProxying() throws Exception
	{
		Resource resource = createCachedResource(60, 1000);
		ItemHandle itemHandle = resource.resolve(Query.fromUriString("test.txt"));
		assertEquals("==>src/test/resources/test.txt", itemHandle.toString());
		assertNotNull(itemHandle.getMetadata());
		assertEquals("text/plain", itemHandle.getMetadata().getMimeType());
		assertFileContent(itemHandle, "abc\n");
	}

	@Test
	public void testCaching() throws Exception
	{
		Resource resource = createCachedResource(60, 1000);
		resource.resolve(Query.fromUriString("test.txt"));
		resource.resolve(Query.fromUriString("test.txt"));
		assertEquals(1, log.size());
	}

	@Test
	public void testExceptionProxying() throws Exception
	{
		Resource resource = createCachedResource(60, 1000);
		try {
			resource.resolve(Query.fromUriString("not_there.txt"));
			fail("should not be reached");
		}
		catch (FileNotFoundException e) {
			assertEquals("src/test/resources/not_there.txt", e.getMessage());
		}
	}

	@Test
	public void testExceptionCaching() throws Exception
	{
		Resource resource = createCachedResource(60, 1000);
		for (int i = 0; i < 3; ++i) {
			try {
				resource.resolve(Query.fromUriString("not_there.txt"));
				fail("should not be reached");
			}
			catch (FileNotFoundException e) {
			}
		}
		assertEquals(1, log.size());
	}

	@Test
	public void testMultiThreadedAccess() throws Exception
	{
		final Resource resource = createCachedResource(0, 4);
		final int[] lock = new int[1];

		synchronized (lock) {
			for (int i = 0; i < 100; ++i) {
				new Thread() {
					@Override
					public void run()
					{
						int n;
						synchronized (lock) {
							n = ++lock[0];
						}

						try {
							for (int i = 0; i < 1000; ++i) {
								assertFileContent(resource.resolve(Query.fromUriString("test.txt?n=" + n)), "abc\n");
								if (i % 100 == 0) {
									Thread.sleep(1);
								}
							}
						}
						catch (Exception e) {
							fail(e.getMessage());
						}
						finally {
							synchronized (lock) {
								if (--lock[0] == 0) {
									lock.notifyAll();
								}
							}
						}
					}
				}.start();
			}
			lock.wait();
		}
	}

	private Resource createCachedResource(int freshness, int byteLimit)
	{
		CachedResource.Config crConfig = new CachedResource.Config();
		crConfig.setResource(createTestResource());
		crConfig.setFreshness(freshness);
		crConfig.setByteLimit(byteLimit);
		return new CachedResource(crConfig);
	}

	private Resource createTestResource()
	{
		return new ProxyResource(new FileResource(new FileResource.Config("src/test/resources/")))
		{
			@Override
			public ItemHandle resolve(Query query) 
				throws IOException
			{
				try {
					ItemHandle result = super.resolve(query);
					log.add(query.toString() + " --> " + result.toString());
					return result;
				}
				catch (IOException e) {
					log.add(query.toString() + " !! " + e.getMessage());
					throw e;
				}
			}
		};
	}

	private void assertFileContent(ItemHandle itemHandle, String expectedContent)
		throws Exception
	{
		Reader reader = itemHandle.openReader();
		try {
			assertNotNull(reader);
			char[] buf = new char[100];
			int cc = reader.read(buf);
			assertEquals(expectedContent, new String(buf, 0, cc));
		}
		finally {
			reader.close();
		}
	}
}
