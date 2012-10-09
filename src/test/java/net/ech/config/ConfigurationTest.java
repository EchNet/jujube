package net.ech.config;

import net.ech.io.*;
import net.ech.io.file.*;
import net.ech.util.*;
import net.ech.service.*;
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
import static org.junit.Assert.fail;

public class ConfigurationTest
{
	private ContentSource configSource = new FileContentSource(new File("config"));

	@Test
	public void testGetString() throws Exception
	{
		ContentQuery configDocSource = new ContentHandleRef(new JsonContentHandle(new Hash("abc", "123")));
		Configuration configuration = new Configuration(configDocSource);
		assertEquals("123", configuration.getString("abc", null));
	}

	@Test
	public void testGetStringDefault() throws Exception
	{
		ContentQuery configDocSource = new ContentHandleRef(new JsonContentHandle(new Hash()));
		Configuration configuration = new Configuration(configDocSource);
		assertNull(configuration.getString("abc", null));
	}

	@Test
	public void testGetBeanReusesBean() throws Exception
	{
		ContentQuery configDocSource = new ContentHandleRef(new JsonContentHandle(new Hash("abc", new Hash())));
		Configuration configuration = new Configuration(configDocSource);
		configuration.installBuilder(new PassThruBuilder());

		Object firstBean = configuration.getBean("abc", Object.class);
		Object secondBean = configuration.getBean("abc", Object.class);

		assertTrue(firstBean == secondBean);
	}

	@Test
	public void testGetBeanReusesBeanIfNotRefreshed() throws Exception
	{
		ContentQuery configDocSource = new ContentHandleRef(new JsonContentHandle(new Hash("abc", new Hash())));
		Configuration configuration = new Configuration(configDocSource);
		configuration.installBuilder(new PassThruBuilder());

		Object firstBean = configuration.getBean("abc", Object.class);
		Object secondBean = configuration.getBean("abc", Object.class);

		assertTrue(firstBean == secondBean);
	}

	@Test
	public void testGetBeanByClass() throws Exception
	{
		ContentQuery configDocSource = new ContentHandleRef(new JsonContentHandle(new Hash("abc", new Hash("foo", "bar"))));
		Configuration configuration = new Configuration(configDocSource);
		configuration.installBuilder(new PassThruBuilder());
		assertNull(configuration.getBean(Object.class));
	}

	@Test
	public void testGetBeanByName() throws Exception
	{
		ContentQuery configDocSource = new ContentHandleRef(new JsonContentHandle(new Hash("abc", new Hash("foo", "bar"))));
		Configuration configuration = new Configuration(configDocSource);
		configuration.installBuilder(new PassThruBuilder());
		assertEquals(new Hash("foo", "bar"), configuration.getBean("abc", Object.class));
	}

	@Test
	public void testGetBeanByNameWrongClass() throws Exception
	{
		ContentQuery configDocSource = new ContentHandleRef(new JsonContentHandle(new Hash("abc", new Hash("foo", "bar"))));
		Configuration configuration = new Configuration(configDocSource);
		configuration.installBuilder(new PassThruBuilder());
		try {
			configuration.getBean("abc", Object.class);
			configuration.getBean("abc", String.class);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("abc: expected java.lang.String, got {foo=bar}", e.getMessage());
		}
	}

	@Test
	public void testGetBeanButNoBuilder() throws Exception
	{
		ContentQuery configDocSource = new ContentHandleRef(new JsonContentHandle(new Hash("abc", new Hash("foo", "bar"))));
		Configuration configuration = new Configuration(configDocSource);
		try {
			configuration.getBean("abc", String.class);
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("Don't know how to build java.lang.String", e.getMessage());
		}
	}

	@Test
	public void testGetBeanOverrideBuilderWithBadClass() throws Exception
	{
		ContentQuery configDocSource = new ContentHandleRef(new JsonContentHandle(new Hash("abc", new Hash("_class", "java.lang.Hahah"))));
		Configuration configuration = new Configuration(configDocSource);
		configuration.installBuilder(new PassThruBuilder());
		try {
			configuration.getBean("abc", Hash.class);
			fail("should not be reached");
		}
		catch (DocumentException e) {
			assertEquals("abc._class: no such class java.lang.Hahah", e.getMessage());
		}
	}

	@Test
	public void testVersionDependsOnVersionOfDocument() throws Exception
	{
		Hash configDoc = new Hash();
		Configuration configuration = new Configuration(new ContentHandleRef(new JsonContentHandle(configDoc)));
		String version = configuration.getVersion();
		configDoc.put("abc", "def");
		assertTrue(!version.equals(configuration.getVersion()));
	}

	@Test
	public void testNewBeanIfNewDocumentVersion() throws Exception
	{
		Hash configDoc = new Hash("abc", new Hash());
		Configuration configuration = new Configuration(new ContentHandleRef(new JsonContentHandle(configDoc)));
		configuration.installBuilder(new NuggetBuilder());
		Nugget firstNugget = configuration.getBean("abc", Nugget.class);
		configDoc.put("def", new Hash()); 
		Nugget secondNugget = configuration.getBean("abc", Nugget.class);
		assertTrue(firstNugget != secondNugget);
	}

	@Test
	public void testCachedBeanIfDocumentUnchanged() throws Exception
	{
		Hash configDoc = new Hash("abc", new Hash());
		Configuration configuration = new Configuration(new ContentHandleRef(new JsonContentHandle(configDoc)));
		configuration.installBuilder(new NuggetBuilder());
		Object firstNugget = configuration.getBean("abc", Nugget.class);
		configDoc.put("abc", new Hash());  // this does not change the document version
		Object secondNugget = configuration.getBean("abc", Nugget.class);
		assertTrue(firstNugget == secondNugget);
	}

	@Test
	public void testBadDocumentSource() throws Exception
	{
		Configuration configuration = new Configuration(new ExplodingContentQuery());

		try {
			configuration.getString("abc", "abc");
			fail("should not be reached");
		}
		catch (IOException e) {
			// Expected.
			assertEquals("Boom.", e.getMessage());
		}
	}

	@Test
	public void testRefreshSynchronization() throws Exception
	{
		final int THREAD_COUNT = 25;
		final SlowContentDocument scq = new SlowContentDocument();
		final Configuration configuration = new Configuration(scq);
		final Countdown lock = new Countdown(THREAD_COUNT);
		
		synchronized (lock) {
			for (int i = 0; i < THREAD_COUNT; ++i) {
				new Thread(new Runnable() {
					public void run() {
						// Force a refresh.
						try { configuration.getString("refreshCount", null); } catch (IOException e) {}
						lock.decrement();
					}
				}).start();
			}
			lock.wait();
		}

		assertEquals(THREAD_COUNT, scq.refreshCount);
	}

	@Test
	public void testGetBeanSynchronization() throws Exception
	{
		final int THREAD_COUNT = 25;

		// Instantiate Configuration
		final Configuration configuration = new Configuration(new ContentHandleRef(new JsonContentHandle(new Hash("abc", new Hash()))));
		// Install a builder for Object.class
		CountingBuilder builder = new CountingBuilder();
		configuration.installBuilder(builder);
		final Countdown lock = new Countdown(THREAD_COUNT);

		// Fire off a number of threads, all of which request the same configured object.
		synchronized (lock) {
			for (int i = 0; i < THREAD_COUNT; ++i) {
				new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(1);
						}
						catch (InterruptedException e) {
						}
						try {
							configuration.getBean("abc", Object.class);
						}
						catch (IOException e) {
							throw new RuntimeException(e);
						}
						lock.decrement();
					}
				}.start();
			}
			lock.wait();
		}
		
		// Verify that builder was called exactly once.
		assertEquals(1, builder.count);
	}

	// Test that the server/cbase.json config file has a "services" definition!
	@Test
	public void testCommonConfig() throws Exception
	{
		Configuration configuration = new Configuration(new ContentSourceQuery(configSource, new ContentRequest("cbase.json")));
		configuration.installBuilder(new NuggetBuilder());
		assertNotNull(configuration.getBean("services", Nugget.class));
	}

	/*******

	// Test that the test config composite has a "test" mode!
	@Test
	public void testTestConfig() throws Exception
	{
		CompositeDocument cdoc = new CompositeDocument();
		cdoc.addSource(new ContentSourceQuery(configSource, new ContentRequest("cbase.json")));
		cdoc.addSource(new ContentSourceQuery(configSource, new ContentRequest("test.json")));
		Configuration configuration = new Configuration(cdoc);
		assertEquals("test", configuration.getString("mode", "ERROR"));
	}

	// Acid test for config document caching.
	@Test
	public void testCachedSingleFileConfigNeedsNoRefresh() throws Exception
	{
		final int PAUSE = 10;
		CompositeDocument comp = new CompositeDocument();
		comp.setFreshnessPeriod(0);
		comp.addSource(new ContentSourceQuery(configSource, new ContentRequest("cbase.json")));
		Configuration config = new Configuration(comp);
		config.installBuilder(new ContentSourceBuilder(config));
		Object bean1 = config.getBean("assetsSource", ContentSource.class);
		Thread.sleep(PAUSE);
		Object bean2 = config.getBean("assetsSource", ContentSource.class);
		assertTrue(bean1 == bean2);
	}

	// Acid test for config document caching.
	@Test
	public void testCachedMultiFileConfigNeedsNoRefresh() throws Exception
	{
		final int PAUSE = 10;
		CompositeDocument comp = new CompositeDocument();
		comp.setFreshnessPeriod(0);
		comp.addSource(new ContentSourceQuery(configSource, new ContentRequest("cbase.json")));
		comp.addSource(new ContentSourceQuery(configSource, new ContentRequest("dev.json")));
		Configuration config = new Configuration(comp);
		config.installBuilder(new ContentSourceBuilder(config));
		Object bean1 = config.getBean("assetsSource", ContentSource.class);
		Thread.sleep(PAUSE);
		Object bean2 = config.getBean("assetsSource", ContentSource.class);
		assertTrue(bean1 == bean2);
	}

	******/

	private static class PassThruBuilder
		implements Builder<Object>
	{
		@Override
		public Class<Object> getClientClass()
		{
			return Object.class;
		}

		@Override
		public Object build(DQuery source) throws IOException
		{
			return source.get();
		}
	}

	private static class NuggetBuilder
		implements Builder<Nugget>
	{
		@Override
		public Class<Nugget> getClientClass()
		{
			return Nugget.class;
		}

		@Override
		public Nugget build(DQuery source) throws IOException
		{
			return new Nugget();
		}
	}

	private static class Nugget
	{
	}

	private static class CountingBuilder
		implements Builder<Object>
	{
		int count;

		@Override
		public Class<Object> getClientClass()
		{
			return Object.class;
		}

		@Override
		public Object build(DQuery source) throws IOException
		{
			return new Hash("count", ++count);
		}
	}

	private static class ExplodingContentQuery
		implements ContentQuery
	{
		@Override
		public ContentHandle query() throws IOException
		{
			throw new IOException("Boom.");
		}
	}

	private static class SlowContentDocument
		implements ContentQuery
	{
		int refreshCount;

		@Override
		public ContentHandle query() throws IOException
		{
			try { Thread.sleep(((int)(Math.random() * 200)) + 1); } catch (Exception e) {}
			synchronized (this) {
				++refreshCount;
				return new JsonContentHandle(new Hash("refreshCount", Integer.toString(refreshCount)));
			}
		}
	}

	private static class Countdown
	{
		int count;

		public Countdown(int count)
		{
			this.count = count;
		}

		synchronized public void decrement()
		{
			if (--count == 0) {
				notifyAll();
			}
		}
	}
}
