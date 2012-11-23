package net.ech.mongo;

import com.mongodb.*;
import java.io.IOException;
import java.util.*;
import java.net.UnknownHostException;
import net.ech.nio.*;

/**
 * Configurable MongoDB database conduit.  Construct one of these per MongoDB instance.
 */
public class MongoDatabase
{
	public final static String DEFAULT_URI = "mongodb://localhost/test";
	public final static int DEFAULT_KEEP_ALIVE_MILLIS = 10 * 1000;

	private MongoURI uri;
	private int keepAliveMillis;
	private int useCount;
	private Mongo mongo;
	private Thread reapThread;

	public static class Config
	{
		private String uri = DEFAULT_URI;
		private int keepAliveMillis = DEFAULT_KEEP_ALIVE_MILLIS;

		public Config()
		{
		}

		public Config(String uri)
		{
			setUri(uri);
		}

		public void setUri(String uri)
		{
			this.uri = uri;
		}

		public void setKeepAliveMillis(int keepAliveMillis)
		{
			this.keepAliveMillis = keepAliveMillis;
		}
	}

	public MongoDatabase(Config config)
		throws IOException
	{
		this.uri = new MongoURI(config.uri);
		this.keepAliveMillis = config.keepAliveMillis;

		if (this.uri.getDatabase() == null) {
			throw new IOException("no DB specified");
		}
	}

	/**
	 * For testing.
	 */
	public synchronized boolean isOpen()
	{
		return mongo != null;
	}

	/**
	 * Get a Mongo collection wrapper.
	 */
	public MongoCollectionActor withCollection(final String collectionName)
		throws IOException
	{
		return new MongoCollectionActor() 
		{
			@Override
			public void act(MongoCollectionAction action)
				throws IOException
			{
				withCollectionDo(collectionName, action);
			}
		};
	}

	@Override
	public String toString()
	{
		// Warning: URI includes authentication info.  Do not leak it out of this class.
		return "mongodb://" + uri.getHosts().get(0) + "/" + uri.getDatabase();
	}

	private void withCollectionDo(String collectionName, MongoCollectionAction action)
		throws IOException
	{
		try {
			action.act(use(collectionName));
		}
		catch (MongoException e) {
			throw new IOException(e);
		}
		finally {
			release();
		}
	}

	private synchronized DBCollection use(String collectionName)
		throws IOException
	{
		++useCount;
		DB db = open();
		interruptReapTask();
		return db.getCollection(collectionName);
	}

	private synchronized void release()
	{
		if (--useCount == 0) {
			reapThread = new Thread(new Reaper());
			reapThread.start();
		}
	}

	private synchronized DB open()
		throws IOException
	{
		if (mongo == null) {
			mongo = new Mongo(uri);
			mongo.setReadPreference(ReadPreference.SECONDARY);
		}

		DB db = mongo.getDB(uri.getDatabase());
		String userName = uri.getUsername();
		if (userName != null && userName.length() > 0 && !db.isAuthenticated()) {
			db.authenticate(userName, uri.getPassword());
		}
		return db;
	}

	private synchronized void interruptReapTask()
	{
		if (reapThread != null) {
			reapThread.interrupt();
			reapThread = null;
		}
	}

	private class Reaper implements Runnable
	{
		public void run()
		{
			try {
				Thread.sleep(keepAliveMillis);
				reap();
			}
			catch (InterruptedException e) {
			}
		}
	}

	public synchronized void reap()
	{
		// Make double sure before closing.
		if (reapThread == Thread.currentThread() && useCount == 0 && mongo != null) {
			mongo.close();
			mongo = null;
		}
	}
}
