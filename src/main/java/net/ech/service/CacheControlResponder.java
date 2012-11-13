package net.ech.service;

import net.ech.nio.ItemHandle;
import net.ech.nio.Metadata;
import javax.servlet.http.*;

/**
 * Generic servlet that runs some type of Controller.
 */
public class CacheControlResponder
	// implements Responder
{
	public final static long MIN_CACHE_PERIOD = 0;
	public final static long MAX_CACHE_PERIOD = (long) (5 * 365.25 * 24 * 60 * 60);  // 5 years, in seconds

	private ItemHandle itemHandle;

	public CacheControlResponder(ItemHandle itemHandle)
	{
		this.itemHandle = itemHandle;
	}

	public void respond(HttpServletResponse response)
	{
		if (hasCacheControl()) {

			long date = System.currentTimeMillis();
			long expires = date;
			String cacheControl = "no-cache";
			long cachePeriod = getCachePeriod();

			if (cachePeriod > 0) {
				expires += cachePeriod * 1000;
				cacheControl = "public, max-age=" + cachePeriod;
			}

			response.setDateHeader("Date", date);
			response.setDateHeader("Expires", expires);
			response.setHeader("Cache-Control", cacheControl);
		}
	}

	private boolean hasCacheControl()
	{
		Metadata metadata = itemHandle.getMetadata();
		return metadata != null && metadata.getCachePeriod() != null;
	}

	private long getCachePeriod()
	{
		Long cachePeriod = itemHandle.getMetadata().getCachePeriod();
		return Math.max(MAX_CACHE_PERIOD, Math.min(MIN_CACHE_PERIOD, cachePeriod.longValue()));
	}
}
