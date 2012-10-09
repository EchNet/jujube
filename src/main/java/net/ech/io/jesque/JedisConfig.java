package net.ech.io.jesque;

import redis.clients.jedis.Jedis;

/**
 * Content drain that enqueues documents onto a Resque queue.
 */
public class JedisConfig 
{
	private String host = "localhost";
	private int port = 6379;
	private int timeout = 15000;   // millis
	private String password;
	private int database = 0;
	private String namespace = "resque";

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getHost()
	{
		return host;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getPort()
	{
		return port;
	}

	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

	public int getTimeout()
	{
		return timeout;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getPassword()
	{
		return password;
	}

	public void setDatabase(int database)
	{
		this.database = database;
	}

	public int getDatabase()
	{
		return database;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}

	public String getNamespace()
	{
		return namespace;
	}

	public Jedis createJedis()
	{
		Jedis jedis = new Jedis(getHost(), getPort(), getTimeout());
		if (getPassword() != null) {
			jedis.auth(getPassword());
		}
		jedis.select(getDatabase());
		return jedis;
	}

	public String toString()
	{
		return "jedis://" + getHost() + ":" + getPort() + "/" + getDatabase();
	}
}
