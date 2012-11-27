package net.ech.service;

import java.io.*;
import java.net.*;
import java.util.*;
import net.ech.config.*;
import net.ech.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ServiceModuleDescriptorTest
{
	@Test
	public void testRecognizeGetResourceServiceModule() throws Exception
	{
		Whence w = new Whence(new Hash()
			.addEntry("thing", new Hash()
				.addEntry("resource", new Hash()
					.addEntry("base", "."))));

		Object bean = w.pull("thing", ServiceModule.class);
		assertNotNull(bean);
		assertTrue(bean instanceof GetResourceServiceModule);
	}
}
