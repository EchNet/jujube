package net.ech.service;

import net.ech.config.*;
import net.ech.io.*;
import net.ech.util.*;
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.junit.*;
import org.springframework.mock.web.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ServletContextManagerTest
{
	private static final String COMMON_CONFIG = "config/common.json";
	private static final String TEST_CONFIG = "config/prod.json";
	private static final String TEST_CONFIG_PATH = COMMON_CONFIG + ";" + TEST_CONFIG;

	@Test
	public void testCreateConfigDocSourceType() throws Exception
	{
		ServletContextManager context = new ServletContextManager(new MockServletConfig()) {
			@Override
			protected ContentQuery createConfigDocSource(String configFilePath) {
				ContentQuery cq = super.createConfigDocSource(TEST_CONFIG_PATH);
				assertTrue(cq instanceof CompositeDocument);
				return endConfigTest();
			}
		};
		triggerConfigTest(context);
	}

	@Test
	public void testCreateConfigDocSourceLength() throws Exception
	{
		ServletContextManager context = new ServletContextManager(new MockServletConfig()) {
			@Override
			protected ContentQuery createConfigDocSource(String configFilePath) {
				ContentQuery cq = super.createConfigDocSource(TEST_CONFIG_PATH);
				assertEquals(2, ((CompositeDocument) cq).getSources().length);
				return endConfigTest();
			}
		};
		triggerConfigTest(context);
	}

	@Test
	public void testCreateConfigDocSourceElement0() throws Exception
	{
		ServletContextManager context = new ServletContextManager(new MockServletConfig()) {
			@Override
			protected ContentQuery createConfigDocSource(String configFilePath) {
				ContentQuery cq = super.createConfigDocSource(TEST_CONFIG_PATH);
				assertEquals("FILE:" + COMMON_CONFIG + ":", ((CompositeDocument) cq).getSources()[0].toString());
				return endConfigTest();
			}
		};
		triggerConfigTest(context);
	}

	@Test
	public void testCreateConfigDocSourceElement1() throws Exception
	{
		ServletContextManager context = new ServletContextManager(new MockServletConfig()) {
			@Override
			protected ContentQuery createConfigDocSource(String configFilePath) {
				ContentQuery cq = super.createConfigDocSource(TEST_CONFIG_PATH);
				assertEquals("FILE:" + TEST_CONFIG + ":", ((CompositeDocument) cq).getSources()[1].toString());
				return endConfigTest();
			}
		};
		triggerConfigTest(context);
	}

	@Test
	public void testCreateConfigDocContents() throws Exception
	{
		ServletContextManager context = new ServletContextManager(new MockServletConfig()) {
			@Override
			protected ContentQuery createConfigDocSource(String configFilePath) {
				return super.createConfigDocSource(TEST_CONFIG_PATH);
			}
		};
		Configuration config = context.getConfiguration();
		ContentHandle handle = config.getContent();
		assertEquals("prod", config.getString("mode", "ERROR"));
	}

	private ContentQuery endConfigTest()
	{
		throw new RuntimeException("HERE");
	}

	private void triggerConfigTest(ServletContextManager context)
	{
		try {
			context.getConfiguration();
			fail("should not be reached");
		}
		catch (RuntimeException e) {
			assertEquals("HERE", e.getMessage());
		}
	}
}
