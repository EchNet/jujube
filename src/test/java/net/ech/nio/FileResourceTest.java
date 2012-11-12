package net.ech.nio;

import java.io.*;
import java.net.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FileResourceTest
{
	private final static String RESOURCES = "src/test/resources/";

	@Test
	public void testToString() throws Exception
	{
		assertEquals("src/test/resources", new FileResource(getFileResourceConfig()).toString());
	}

	@Test
	public void testPlainTextFile() throws Exception
	{
		runTextFileTest(new FileResource(getFileResourceConfig()), "test.txt", "abc\n", RESOURCES + "test.txt");
		runTextFileTest(new FileResource(getFileResourceConfig()), "not_found.txt", null, RESOURCES + "not_found.txt");
	}

	@Test
	public void testUriComponentsOtherThanPathIgnored() throws Exception
	{
		runTextFileTest(new FileResource(getFileResourceConfig()), "file://auth/test.txt#ignored", "abc\n", RESOURCES + "test.txt");
		runTextFileTest(new FileResource(getFileResourceConfig()), "file://auth/not_found.txt#ignored", null, RESOURCES + "not_found.txt");
	}

	@Test
	public void testImplicitFileExtension() throws Exception
	{
		FileResource.Config config = getFileResourceConfig();
		config.setExtension(".txt");
		runTextFileTest(new FileResource(config), "test", "abc\n", RESOURCES + "test.txt");
		runTextFileTest(new FileResource(config), "not_found", null, RESOURCES + "not_found.txt");
	}

	@Test
	public void testReplaceFileExtension() throws Exception
	{
		FileResource.Config config = getFileResourceConfig();
		config.setExtension(".txt");
		config.setIgnoreQueryExtension(true);
		runTextFileTest(new FileResource(config), "test.ign", "abc\n", RESOURCES + "test.txt");
		runTextFileTest(new FileResource(config), "test", "abc\n", RESOURCES + "test.txt");
		runTextFileTest(new FileResource(config), "not_found.ign", null, RESOURCES + "not_found.txt");
	}

	@Test
	public void testFixedMimeType() throws Exception
	{
		FileResource.Config config = getFileResourceConfig();
		config.setMimeType("text/plain");
		runTextFileTest(new FileResource(config), "test.css", "abc {\n\ttext-align: center;\n}\n", RESOURCES + "test.css");
	}

	@Test
	public void testJsonMimeType() throws Exception
	{
		ItemHandle itemHandle = new FileResource(getFileResourceConfig()).resolve(new Query("test.json"));
		assertEquals("application/json", itemHandle.getMetadata().getMimeType());
	}

	@Test
	public void testJavascriptMimeType() throws Exception
	{
		ItemHandle itemHandle = new FileResource(getFileResourceConfig()).resolve(new Query("test.js"));
		assertEquals("application/x-javascript", itemHandle.getMetadata().getMimeType());
	}

	@Test
	public void testCssMimeType() throws Exception
	{
		ItemHandle itemHandle = new FileResource(getFileResourceConfig()).resolve(new Query("test.css"));
		assertEquals("text/css", itemHandle.getMetadata().getMimeType());
	}

	@Test
	public void testGifMimeType() throws Exception
	{
		ItemHandle itemHandle = new FileResource(getFileResourceConfig()).resolve(new Query("test.gif"));
		assertEquals("image/gif", itemHandle.getMetadata().getMimeType());
	}

	@Test
	public void testPngMimeType() throws Exception
	{
		ItemHandle itemHandle = new FileResource(getFileResourceConfig()).resolve(new Query("test.png"));
		assertEquals("image/png", itemHandle.getMetadata().getMimeType());
	}

	@Test
	public void testDefaultMimeType() throws Exception
	{
		ItemHandle itemHandle = new FileResource(getFileResourceConfig()).resolve(new Query("test.properties"));
		assertEquals("text/plain", itemHandle.getMetadata().getMimeType());
	}

	@Test
	public void testDefaultMimeType2() throws Exception
	{
		runTextFileTest(new FileResource(getFileResourceConfig()), "test", "abc\n", RESOURCES + "test");
	}

	@Test
	public void testDirectoryAndEmptyPath() throws Exception
	{
		try {
			new FileResource(getFileResourceConfig()).resolve(new Query(""));
			fail("should not be reached");
		}
		catch (IOException e) {
			assertEquals("src/test/resources: is directory", e.getMessage());
		}
	}
	
	private FileResource.Config getFileResourceConfig()
	{
		return new FileResource.Config(RESOURCES);
	}

	private void runTextFileTest(Resource resource, String uriString, String expectedContent, String expectedItemPath)
		throws Exception
	{
		try {
			Query query = new Query(uriString);
			ItemHandle itemHandle = resource.resolve(query);
			if (expectedContent == null) {
				fail("should not be reached");
			}
			else {
				assertEquals(expectedItemPath, itemHandle.toString());
				assertTrue(itemHandle.isLatent());
				assertNotNull(itemHandle.getMetadata());
				assertEquals("text/plain", itemHandle.getMetadata().getMimeType());
				assertEquals("UTF-8", itemHandle.getMetadata().getCharacterEncoding());
				assertFileContent(itemHandle, expectedContent);
			}
		}
		catch (FileNotFoundException e) {
			if (expectedContent != null) {
				throw e;
			}
			assertEquals(expectedItemPath, e.getMessage());
		}
	}

	private void assertFileContent(ItemHandle itemHandle, String expectedContent)
		throws Exception
	{
		Reader reader = itemHandle.presentReader();
		assertNotNull(reader);
		char[] buf = new char[100];
		int cc = reader.read(buf);
		assertEquals(expectedContent, new String(buf, 0, cc));
	}
}
