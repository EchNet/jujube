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
	private final static int KEEP_ALIVE_MILLIS = 10 * 1000;

	private MongoURI uri;
	private MongoOptions options;
	private int useCount;
	private Mongo mongo;
	private Thread reapThread;
	private ErrorLog errorLog;

	public static class Config extends MongoOptions
	{
		private String uri = "mongo://localhost";
		private ErrorLog errorLog = new ErrorLog();

		public Config()
		{
			reset();
		}

		public void setUri(String uri)
		{
			this.uri = uri;
		}

		public void setErrorLog(ErrorLog errorLog)
		{
			this.errorLog = errorLog;
		}
	}

	public MongoDatabase(Config config)
		throws IOException
	{
		this.uri = new MongoURI(config.uri);
		this.options = config.copy();
		this.errorLog = config.errorLog;

		if (this.uri.getDatabase() == null) {
			throw new IOException("no DB specified");
		}
	}

	/**
	 * Get a Mongo collection wrapper.
	 */
	public MongoCollectionActor withCollection(String collectionName)
		throws IOException
	{
		final String collName = collectionName == null ? uri.getCollection() : collectionName;
		if (collName == null) {
			throw new IOException("no collection name");
		}

		return new MongoCollectionActor() 
		{
			@Override
			public void act(MongoCollectionAction action)
				throws IOException
			{
				withCollectionDo(collName, action);
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
		DB db = open();
		++useCount;
		interruptReapTask();
		return db.getCollection(collectionName);
	}

	private synchronized void release()
	{
		switch (useCount) {
		case 0:
			throw new IllegalStateException();
		case 1:
			startReapTask();
		default:
			--useCount;
		}
	}

	private synchronized DB open()
		throws IOException
	{
		try {
			if (mongo == null) {
				mongo = new Mongo(uri);
				mongo.setReadPreference(ReadPreference.SECONDARY);
			}

			String database = uri.getDatabase();
			DB db = mongo.getDB(database);

			String userName = uri.getUsername();
			if (userName != null && userName.length() > 0 && !db.isAuthenticated()) {
				db.authenticate(userName, uri.getPassword());
			}

			return db;
		}
		catch (UnknownHostException e) {
			throw new IOException(e);
		}
		catch (MongoException e) {
			throw new IOException(e);
		}
	}

	private synchronized void close()
	{
		if (mongo != null) {
			mongo.close();
			mongo = null;
		}
	}

	private synchronized void interruptReapTask()
	{
		if (reapThread != null) {
			reapThread.interrupt();
			reapThread = null;
		}
	}

	private synchronized void startReapTask()
	{
		reapThread = new Thread(new Reaper());
		reapThread.start();
	}

	private class Reaper implements Runnable
	{
		public void run()
		{
			try {
				Thread.sleep(KEEP_ALIVE_MILLIS);
				reap();
			}
			catch (InterruptedException e) {
			}
		}
	}

	public synchronized void reap()
	{
		// Make double sure before closing.
		if (reapThread == Thread.currentThread() && useCount == 0) {
			close();
		}
	}
}
