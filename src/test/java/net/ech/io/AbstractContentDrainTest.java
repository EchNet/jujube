package net.ech.io;

import net.ech.util.*;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AbstractContentDrainTest
{
	@Test
	public void testAccept() throws Exception
	{
		assertEquals(new Hash(), new AbstractContentDrain(){}.accept(null).getDocument());
	}
}
