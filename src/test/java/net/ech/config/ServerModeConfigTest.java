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

// Test to see that all of the standard server config docs parse and look sane.
public class ServerModeConfigTest
{
	private ContentSource configSource = new FileContentSource(new File("config"));

	@Test
	public void testDevModeConfig() throws Exception
	{
		CompositeDocument comp = new CompositeDocument();
		comp.addSource(new ContentSourceQuery(configSource, new ContentRequest("cbase.json")));
		comp.addSource(new ContentSourceQuery(configSource, new ContentRequest("dev.json")));
		assertEquals("dev", new DQuery(comp.query().getDocument()).find("mode").get(String.class));
	}

	@Test
	public void testProdModeConfig() throws Exception
	{
		CompositeDocument comp = new CompositeDocument();
		comp.addSource(new ContentSourceQuery(configSource, new ContentRequest("cbase.json")));
		comp.addSource(new ContentSourceQuery(configSource, new ContentRequest("prod.json")));
		assertEquals("prod", new DQuery(comp.query().getDocument()).find("mode").get(String.class));
	}
}
