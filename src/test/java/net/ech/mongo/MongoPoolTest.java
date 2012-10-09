package net.ech.mongo;

import com.mongodb.*;
import net.ech.util.*;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MongoPoolTest
{
	MongoPool dbPool;

	@Before
	public void setUp()
	{
        dbPool = new MongoPool();
	}
}
