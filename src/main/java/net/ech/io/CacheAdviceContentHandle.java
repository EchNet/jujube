package net.ech.io;

public class CacheAdviceContentHandle
	extends ProxyContentHandle
	implements ContentHandle
{
	private CacheAdvice cacheAdvice;

	public CacheAdviceContentHandle(ContentHandle inner, CacheAdvice cacheAdvice)
	{
		super(inner);
		this.cacheAdvice = cacheAdvice;
	}

	@Override
	public CacheAdvice getCacheAdvice()
	{
		return cacheAdvice;
	}
}
