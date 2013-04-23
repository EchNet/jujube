package net.ech.doc;

import java.util.*;
import net.ech.util.Hash;
import org.junit.*;
import static org.junit.Assert.*;

public class DocumentDigesterTest
{
	@Test
	public void testSillyAlgorithm() throws Exception
	{
		try {
			new DocumentDigester(new Document(null), "Simeonov's Special");
			fail("should not be reached");
		}
		catch (java.security.NoSuchAlgorithmException e) {
			// expected
		}
	}

	@Test
	public void testItThroughItsPaces() throws Exception
	{
		Document doc = new Document(
			new Hash()
				.addEntry("string", "String")
				.addEntry("null", null)
				.addEntry("date", new Date())
				.addEntry("bool", false)
				.addEntry("int", 546)
				.addEntry("float", 54.6)
				.addEntry("map", new Hash())
				.addEntry("list", Arrays.asList(new Object[] { "The", 39, "Steps" }))
		);

		byte[] digest = new DocumentDigester(doc, "MD5").walk().getDigest();
		assertNotNull(digest);
		assertEquals(16, digest.length);
	}
}
