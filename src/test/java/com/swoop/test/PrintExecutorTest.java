package com.swoop.test;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import net.ech.config.Whence;
import net.ech.json.JsonDeserializer;
import org.junit.*;
import static org.junit.Assert.*;

public class PrintExecutorTest
{
	// from whence configured objects come...
	Whence w;

	@Before
	public void setUp() throws Exception
	{
		// Create an object source that loads from a single JSON document.
		Reader reader = new InputStreamReader(PrintExecutorTest.class.getClassLoader().getResourceAsStream("hello.json"));
		try {
			w = new Whence(new JsonDeserializer().read(reader));
		}
		finally {
			reader.close();
		}
	}

	@Test
	public void testNotFoundCase() throws Exception
	{
		try {
			w.configure("thing", Object.class);
		}
		catch (IOException e) {
			assertEquals("thing: no such key", e.getMessage());
		}
	}

	@Test
	public void testPrintExecutor1Config() throws Exception
	{
		PrintExecutor pe = w.configure("hello1", PrintExecutor.class);
		assertNotNull(pe);
		assertEquals(2, pe.getSequences().size());
		assertEquals(3, pe.getWorkers().size());
	}

	@Test
	public void testPrintExecutor2Config() throws Exception
	{
		PrintExecutor pe = w.configure("hello2", PrintExecutor.class);
		assertNotNull(pe);
		assertEquals(3, pe.getSequences().size());
		assertEquals(4, pe.getWorkers().size());
	}

	@Test
	public void testPrintExecutor1DefaultBehavior() throws Exception
	{
		PrintExecutor pe = w.configure("hello1", PrintExecutor.class);
		assertEquals("Hello, World!\n", pe.run().getOutput());
	}

	@Test
	public void testPrintExecutor1OverrideBehavior() throws Exception
	{
		PrintExecutor pe = w.configure("hello1", PrintExecutor.class);
		Map<String,String> params = new HashMap<String,String>();
		params.put("sequence", "backwards");
		assertEquals("World!, Hello\n", pe.run(params).getOutput());
	}

	@Test
	public void testPrintExecutor2DefaultBehavior() throws Exception
	{
		PrintExecutor pe = w.configure("hello2", PrintExecutor.class);
		assertEquals("Hello\nWorld!\n", pe.run().getOutput());
	}

	@Test
	public void testPrintExecutor2InheritedBehavior() throws Exception
	{
		PrintExecutor pe = w.configure("hello2", PrintExecutor.class);
		Map<String,String> params = new HashMap<String,String>();
		params.put("sequence", "backwards");
		assertEquals("World!, Hello\n", pe.run(params).getOutput());
	}

	@Test
	public void testPrintExecutor2NewBehavior() throws Exception
	{
		PrintExecutor pe = w.configure("hello2", PrintExecutor.class);
		Map<String,String> params = new HashMap<String,String>();
		params.put("sequence", "alt");
		assertEquals("Helloooooo... World!\n", pe.run(params).getOutput());
	}
}
