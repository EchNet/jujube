package net.ech.io.jesque;

import net.ech.io.*;
import net.ech.io.template.*;
import net.ech.util.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Content drain that enqueues documents onto a Resque queue.
 */
public class JesqueContentDrain 
	extends AbstractContentDrain
{
	private JedisConfig config;
	private Jedis jedis;
	private String queueName;
	private String jobName;

	public JesqueContentDrain(JedisConfig config, String queueName, String jobName)
	{
		this.config = config;
		this.jedis = config.createJedis();
		this.queueName = queueName;
		this.jobName = jobName;
	}

	/**
	 * Enqueues the content item onto a Resque queue.
	 * @param contentHandle a handle on the content item.
	 * @return an empty hash.
	 */
	@Override
	public ContentHandle accept(ContentHandle contentHandle)
		throws IOException
	{
		enqueue(toJobJsonString(contentHandle));
		return new JsonContentHandle(new Hash());
	}

	//
	// Write the content structure into a String.  Use the write method, not the
	// getDocument method, of the content handle, to avoid unnecessary parsing.
	//
	public String toJobJsonString(final ContentHandle contentHandle)
		throws IOException
	{
		StringWriter jobJsonWriter = new StringWriter();
		final String ARG = "arg";
		ContentHandle template = new JsonContentHandle(new Hash()
			.addEntry("class", jobName)
			.addEntry("args", new Object[] { "{{CONTENT}}" }));
		Evaluator evaluator = new StaticEvaluator(contentHandle);
		new TemplateContentHandle(template, evaluator).write(jobJsonWriter);
		return jobJsonWriter.toString();
	}

	public void enqueue(String jobJson)
		throws IOException
	{
		synchronized (jedis) {
			if (!responsive()) {
				try {
					jedis.connect();
				}
				catch (JedisConnectionException e) {
					if (e.getCause() instanceof IOException) 
						throw (IOException) e.getCause();
					throw e;
				}
				if (!responsive()) {
					throw new IOException("Cannot connect to " + config);
				}
			}
			jedis.sadd(config.getNamespace() + ":queues", queueName);
			jedis.rpush(config.getNamespace() + ":queue:" + queueName, jobJson);
		}
	}

	private boolean responsive()
	{
		return jedis.isConnected() && ping();
	}

	private boolean ping()
	{
		try {
			if ("PONG".equals(jedis.ping())) {
				return true;
			}
		}
		catch (JedisConnectionException e) {
		}
		jedis.disconnect();
		return false;
	}
}
