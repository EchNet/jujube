package net.ech.io;

import net.ech.codec.*;
import net.ech.util.*;
import net.ech.service.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CachingContentSourceTest
{
	CachingContentSource contentSource;

	@Before
	public void setUp() throws Exception
	{
		// Basis for testing - a caching content source that resolves each new request to a content item consisting
		// of a copy of the request parameters.
		contentSource = new CachingContentSource(new AbstractContentSource()
		{
			@Override
			public ContentHandle resolve(ContentRequest request)
				throws IOException
			{
				if (request.getPath().equals("THROW")) {
					throw new IOException();
				}
				return new JsonContentHandle(new DQuery(request.getParameters()).copyDoc().get());
			}
		});
	}

	@Test
	public void testBasicIntegrity() throws Exception
	{
		// Verify that the ContentSource does what it's supposed to do.
		Hash parameters = new Hash().addEntry("a", 1).addEntry("b", 2).addEntry("c", 3);
		assertEquals(parameters, contentSource.resolve(new ContentRequest("", parameters)).getDocument());
	}

	@Test
	public void testBuffering() throws Exception
	{
		// Verify that the ContentSource returns a BufferedContentHandle, but not the one created by the 
		// dummy content source above.
		ContentHandle result = contentSource.resolve(new ContentRequest("", new Hash()));
		assertTrue((result instanceof BufferedContentHandle) && !(result instanceof JsonContentHandle));
	}

	@Test
	public void testCaching() throws Exception
	{
		// Set up source to cache one.
		contentSource.setCacheSize(1);
		contentSource.setFreshness(1000 * 1000);

		// Test that the same handle comes back the second time.
		Hash parameters = new Hash().addEntry("a", 1).addEntry("b", 2).addEntry("c", 3);
		ContentHandle h1 = contentSource.resolve(new ContentRequest("", parameters));
		ContentHandle h2 = contentSource.resolve(new ContentRequest("", parameters));
		assertTrue(h1 == h2);
	}

	@Test
	public void testLru() throws Exception
	{
		// Set up source to cache one.
		contentSource.setCacheSize(1);
		contentSource.setFreshness(1000 * 1000);

		// Test that a new handle is created after the cache overflows.
		Hash parameters1 = new Hash().addEntry("a", 1).addEntry("b", 2);
		Hash parameters2 = new Hash().addEntry("b", 2).addEntry("c", 3);
		ContentHandle h1 = contentSource.resolve(new ContentRequest("", parameters1));
		contentSource.resolve(new ContentRequest("", parameters2));
		ContentHandle h2 = contentSource.resolve(new ContentRequest("", parameters1));
		assertTrue(h1 != h2);
	}

	@Test
	public void testCachingOfException() throws Exception
	{
		// Set up source to cache one.
		contentSource.setCacheSize(1);
		contentSource.setFreshness(1000 * 1000);

		// Test that the same handle comes back the second time.
		Hash parameters = new Hash().addEntry("a", 1).addEntry("b", 2).addEntry("c", 3);
		IOException exception1;
		IOException exception2;
		try {
			contentSource.resolve(new ContentRequest("THROW", parameters));
			fail("should not be reached");
			return;
		}
		catch (IOException e) {
			exception1 = e;
		}
		try {
			contentSource.resolve(new ContentRequest("THROW", parameters));
			fail("should not be reached");
			return;
		}
		catch (IOException e) {
			exception2 = e;
		}

		assertTrue(exception1 == exception2);
	}

	@Test
	public void testMultiThreadedAccess() throws Exception
	{
		// Set up source to cache one.
		contentSource.setCacheSize(3);
		contentSource.setFreshness(8);

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

						Map<String,Object> parameters = new Hash("n", n);

						try {
							for (int i = 0; i < 1000; ++i) {
								assertEquals(parameters, contentSource.resolve(new ContentRequest("", parameters)).getDocument());
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
}
