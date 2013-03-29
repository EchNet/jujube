package com.swoop.test;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import net.ech.config.Whence;
import net.ech.json.JsonDeserializer;
import net.ech.util.Hash;
import org.junit.*;
import static org.junit.Assert.*;

public class PrintExecutorTest
{
	Whence w;

	@Before
	public void setUp() throws Exception
	{
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
			assertEquals("cannot configure thing: no such key", e.getMessage());
		}
	}

	@Test
	public void testPrintExecutor1Instantiated() throws Exception
	{
		PrintExecutor pe = w.configure("hello1", PrintExecutor.class);
		assertNotNull(pe);
		assertTrue(pe.getSequences().size() > 0);
		assertTrue(pe.getWorkers().size() > 0);
	}

	@Test
	public void testPrintExecutor2Instantiated() throws Exception
	{
		PrintExecutor pe = w.configure("hello2", PrintExecutor.class);
		assertNotNull(pe);
		assertTrue(pe.getSequences().size() > 0);
		assertTrue(pe.getWorkers().size() > 0);
	}
}
