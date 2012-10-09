package net.ech.service;

import net.ech.config.*;
import net.ech.io.*;
import net.ech.io.file.*;
import net.ech.util.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.junit.*;
import org.springframework.mock.web.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class TemplateExpressionEvaluatorTest
	implements ConfigConstants
{
	ContentRequest request;

	@Before
	public void setUp() throws Exception
	{
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setPathInfo("/path/info");
		httpRequest.addParameter("a", "1");
		httpRequest.addParameter("b", "2");
		
		Configuration configuration = new Configuration(new ContentHandleRef(new JsonContentHandle(new Hash())));

		request = ContentRequestManager.createContentRequest(new RequestWrapper(httpRequest, "PATH"), configuration);
	}

	@Test
	public void testContentTypeJson() throws Exception
	{
		assertEquals("application/json", eval(" request.path ").getCodec().getContentType());
	}

	@Test
	public void testEmptyInput() throws Exception
	{
		try {
			eval("");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("empty expression", e.getMessage());
		}
	}

	@Test
	public void testHttpRequestPath() throws Exception
	{
		assertEquals("path/info", eval(" httpRequest.path ").getDocument());
	}

	@Test
	public void testPathInfo() throws Exception
	{
		assertEquals("PATH", eval(" pathInfo ").getDocument());
	}

	@Test
	public void testBadIndex() throws Exception
	{
		assertNull(eval(" request.something_not_there ").getDocument());
	}

	@Test
	public void testMeaninglessFunctionCall() throws Exception
	{
		try {
			eval(" assetBase() ");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("not a function", e.getMessage());
		}
	}

	@Test
	public void testLoadMissingArg() throws Exception
	{
		try {
			eval(" load() ");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("load requires arg", e.getMessage());
		}
	}

	@Test
	public void testLoadBadUri() throws Exception
	{
		try {
			eval(" load(' what in tarnation? ') ");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertTrue(e.getCause() instanceof java.net.URISyntaxException);
		}
	}

	@Test
	public void testLoadMissingAuthority() throws Exception
	{
		try {
			eval(" load( '/path' ) ");
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("/path: authority required", e.getMessage());
		}
	}

	private ContentHandle eval(String expr) throws Exception
	{
		Map<String,ContentSource> authorities = new HashMap<String,ContentSource>();
		authorities.put("test", new FileContentSource(new File("src/test/resources/")));
		authorities.put("test1", new FileContentSource(new File("src/test/resources/")));
		Map<String,String> mappings = new HashMap<String,String>();
		mappings.put("test1", "surprise/surprise");
		ContentManager contentManager = new ContentManager(authorities, mappings);

		return new TemplateExpressionEvaluator(request, contentManager).evaluateExpression(expr);
	}
}
