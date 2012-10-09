package net.ech.mongo;

import net.ech.config.*;
import net.ech.util.*;

public class MongoPoolBuilder
	extends AbstractBuilder<MongoPool>
{
	public MongoPoolBuilder(Configuration configuration)
	{
		super(configuration);
	}

	@Override
	public Class<MongoPool> getClientClass()
	{
		return MongoPool.class;
	}

	@Override
	protected MongoPool buildFromNull(DQuery dq)
	{
		return new MongoPool();
	}
}
