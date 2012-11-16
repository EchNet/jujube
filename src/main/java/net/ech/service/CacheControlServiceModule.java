package net.ech.service;

import net.ech.nio.ItemHandle;
import net.ech.nio.Metadata;
import javax.servlet.http.*;

public class CacheControlServiceModule
	extends AbstractServiceModule
	implements ServiceModule
{
	public final static long MIN_CACHE_PERIOD = 0;
	public final static long MAX_CACHE_PERIOD = (long) (5 * 365.25 * 24 * 60 * 60);  // 5 years, in seconds

	@Override
	public void postprocess(ItemHandle contentItemHandle)
	{
		if ("GET".equals(getRequest().getMethod()) &&
			hasCacheControl(contentItemHandle)) {

			long date = System.currentTimeMillis();
			long expires = date;
			String cacheControl = "no-cache";
			long cachePeriod = getCachePeriod(contentItemHandle);

			if (cachePeriod > 0) {
				expires += cachePeriod * 1000;
				cacheControl = "public, max-age=" + cachePeriod;
			}

			getResponse().setDateHeader("Date", date);
			getResponse().setDateHeader("Expires", expires);
			getResponse().setHeader("Cache-Control", cacheControl);
		}
	}

	private boolean hasCacheControl(ItemHandle itemHandle)
	{
		Metadata metadata = itemHandle.getMetadata();
		return metadata != null && metadata.getCachePeriod() != null;
	}

	private long getCachePeriod(ItemHandle itemHandle)
	{
		Long cachePeriod = itemHandle.getMetadata().getCachePeriod();
		return Math.max(MAX_CACHE_PERIOD, Math.min(MIN_CACHE_PERIOD, cachePeriod.longValue()));
	}
}
