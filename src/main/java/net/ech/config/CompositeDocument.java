package net.ech.config;

import net.ech.io.*;
import net.ech.util.*;
import java.io.*;
import java.util.*;

public class CompositeDocument
	implements ContentQuery
{
	private final static int DEFAULT_FRESHNESS_PERIOD = 3000;

	private List<ContentQuery> sources = new ArrayList<ContentQuery>();
	private Object document;
	private String version;
	private int freshnessPeriod = DEFAULT_FRESHNESS_PERIOD;
    private Object refreshLock = new Object();
    private long lastRefresh;
	private IOException lastError;

	public void addSource(ContentQuery source)
	{
		sources.add(source);
	}

	public void setFreshnessPeriod(int millis)
	{
		this.freshnessPeriod = millis;
	}

	public ContentQuery[] getSources()
	{
		return sources.toArray(new ContentQuery[sources.size()]);
	}

	@Override
	public ContentHandle query()
		throws IOException
	{
		refresh();
		if (lastError != null) {
			throw lastError;
		}
		return new JsonContentHandle(document)
		{
			@Override
			public String getVersion()
			{
				return version;
			}
		};
	}

    private void refresh()
    {
        if (System.currentTimeMillis() - lastRefresh > freshnessPeriod) {
            synchronized (refreshLock) {
				if (System.currentTimeMillis() - lastRefresh > freshnessPeriod) {
					doRefresh();
				}
			}
		}
	}

	private void doRefresh()
	{
		this.lastError = null;
		try {
			List<ContentHandle> contentHandles = resolveAllSources();
			String newVersion = getCompositeVersion(contentHandles);
			if (!newVersion.equals(this.version)) {
				this.version = newVersion;
				this.document = getCompositeDocument(contentHandles);
			}
		}
		catch (IOException e) {
			lastError = e;
		}

		// Do the following afterward; otherwise, all threads check for updates at once.
		this.lastRefresh = System.currentTimeMillis();
	}

	private List<ContentHandle> resolveAllSources()
		throws IOException
	{
		List<ContentHandle> list = new ArrayList<ContentHandle>();
		for (ContentQuery source : sources) {
			try {
				ContentHandle content = source.query();
				if (!ContentTypes.isJson(content.getCodec().getContentType())) {
					throw new DocumentException(content.getSource() + ": not JSON");
				}
				list.add(content);
			}
			catch (FileNotFoundException e) {
			}
		}
		return list;
	}

	private String getCompositeVersion(List<ContentHandle> contentHandles)
		throws IOException
	{
		StringBuilder version = new StringBuilder();
		for (ContentHandle contentHandle : contentHandles) {
			if (version.length() == 0) {
				version.append(",");
			}
			String thisVersion = contentHandle.getVersion();
			if (thisVersion == null) {
				thisVersion = Long.toString(System.currentTimeMillis());  // Defeat version caching.
			}
			version.append(thisVersion);
		}
		return version.toString();
	}

	private Object getCompositeDocument(List<ContentHandle> contentHandles)
		throws IOException
	{
		DQuery doc = new DQuery(null);
		for (ContentHandle contentHandle : contentHandles) {
			try {
				doc = doc.extend(new DQuery(contentHandle.getDocument()));
			}
			catch (IOException e) {
				throw new IOException(contentHandle.getSource() + ": badly formed configuration document", e);
			}
		}
		return doc.get();
	}
}
