package com.swoop.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.ech.config.DocumentBasedConfigurator;
import net.ech.doc.Document;
import net.ech.doc.DocumentResolver;
import net.ech.doc.ChildDocumentResolver;
import net.ech.doc.ExternalDocumentResolver;
import org.junit.*;
import static org.junit.Assert.*;

public class PrintExecutorTest
{
	DocumentResolver docResolver;

	@Before
	public void setUp() throws Exception
	{
		// Create a document source that loads from a single JSON document.
		ExternalDocumentResolver genResolver = new ExternalDocumentResolver();
		Document doc = genResolver.resolve("resource:hello").produce();
		docResolver = new ChildDocumentResolver(doc);
	}

	@Test
	public void testNotFoundCase() throws Exception
	{
		try {
			configure("thing");
		}
		catch (IOException e) {
			assertEquals("hello.json (resource:hello): .thing: child document not found", e.getMessage());
		}
	}

	@Test
	public void testPrintExecutor1Config() throws Exception
	{
		PrintExecutor pe = configure("hello1");
		assertNotNull(pe);
		assertEquals(2, pe.getSequences().size());
		assertEquals(3, pe.getWorkers().size());
	}

	@Test
	public void testPrintExecutor2Config() throws Exception
	{
		PrintExecutor pe = configure("hello2");
		assertNotNull(pe);
		assertEquals(3, pe.getSequences().size());
		assertEquals(4, pe.getWorkers().size());
	}

	@Test
	public void testPrintExecutor1DefaultBehavior() throws Exception
	{
		PrintExecutor pe = configure("hello1");
		assertEquals("Hello, World!\n", pe.run().getOutput());
	}

	@Test
	public void testPrintExecutor1OverrideBehavior() throws Exception
	{
		PrintExecutor pe = configure("hello1");
		Map<String,String> params = new HashMap<String,String>();
		params.put("sequence", "backwards");
		assertEquals("World!, Hello\n", pe.run(params).getOutput());
	}

	@Test
	public void testPrintExecutor2DefaultBehavior() throws Exception
	{
		PrintExecutor pe = configure("hello2");
		assertEquals("Hello\nWorld!\n", pe.run().getOutput());
	}

	@Test
	public void testPrintExecutor2InheritedBehavior() throws Exception
	{
		PrintExecutor pe = configure("hello2");
		Map<String,String> params = new HashMap<String,String>();
		params.put("sequence", "backwards");
		assertEquals("World!, Hello\n", pe.run(params).getOutput());
	}

	@Test
	public void testPrintExecutor2NewBehavior() throws Exception
	{
		PrintExecutor pe = configure("hello2");
		Map<String,String> params = new HashMap<String,String>();
		params.put("sequence", "alt");
		assertEquals("Helloooooo... World!\n", pe.run(params).getOutput());
	}

	private PrintExecutor configure(String name)
		throws Exception
	{
		return new DocumentBasedConfigurator(name, docResolver).configure(PrintExecutor.class);
	}
}
