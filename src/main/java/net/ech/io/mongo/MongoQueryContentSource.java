package net.ech.io.mongo;

import com.mongodb.*;
import net.ech.io.*;
import net.ech.util.*;
import net.ech.mongo.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Implementation of ContentSource based on querying a specific MongoDB collection.
 */
public class MongoQueryContentSource
	extends AbstractContentSource
	implements ContentSource
{
	private MongoCollection collection;
	private String[] pathFields = new String[] { "_id" };
	private String[] listingFields = new String[0];

	/**
	 * Constructor.
	 * @param collection  Mongo collection handle
	 */
	public MongoQueryContentSource(MongoCollection collection)
	{
		this.collection = collection;
	}

	public void setPathFields(String[] pathFields)
	{
		this.pathFields = pathFields;
	}

	public void setListingFields(String[] listingFields)
	{
		this.listingFields = listingFields;
	}

	@Override
	public ContentHandle resolve(final ContentRequest request)
		throws IOException
	{
		// Decompose request.
		final String path = getRequestPath(request);
		final Map<String,Object> parameters = request.getParameters();

		final StrongReference<Object> objRef = new StrongReference<Object>();

		collection.act(new MongoCollectionAction() {
			@Override
			public void act(DBCollection dbc)
				throws IOException, MongoException
			{
				// Compose a query.
				DBObject q = new BasicDBObject();
				if (addPathFields(q, path) != null) {
					throw new IOException(path + ": does not resolve to a single item");
				}
				addParameters(q, parameters);

				// HACK: this filter is specifically for domain configs.  Should be conditional.
				DBObject filter = new BasicDBObject();
				filter.put("versions", 0);

				objRef.set(dbc.findOne(q, filter));
			}
		});

		if (objRef.get() == null) {
			throw new FileNotFoundException(request.getPath());
		}

		return new JsonContentHandle(request.getPath(), objRef.get());
	}

	@Override
	public Object[] list(final String path)
		throws IOException
	{
		final StrongReference<Object> objRef = new StrongReference<Object>();

		collection.act(new MongoCollectionAction() {
			@Override
			public void act(DBCollection dbc)
				throws IOException, MongoException
			{
				DBObject q = new BasicDBObject();
				String idField = addPathFields(q, path);
				DBObject filter = new BasicDBObject();
				filter.put(idField, 1);
				for (String field : listingFields) {
					filter.put(field, 1);
				}
				DBObject[] listing = dbc.find(q, filter).toArray().toArray(new DBObject[0]);
				cleanUpListing(listing, idField);
				sortListing(listing);
				objRef.set(listing);
			}
		});

		return (Object[]) objRef.get();
	}

	private String getRequestPath(ContentRequest request)
		throws IOException
	{
		String uriString = request.getPath();
		try {
			URI uri = new URI(uriString);
			String path = uri.getPath();
			return path == null || !path.startsWith("/") ? path : path.substring(1);
		}
		catch (URISyntaxException e) {
			throw new IOException(uriString, e);
		}
	}

	private void addParameters(DBObject q, Map<String,Object> parameters)
	{
		for (Map.Entry<String,Object> entry : parameters.entrySet()) {
			q.put(entry.getKey(), entry.getValue());
		}
	}

	private String composeSource(String path, Map<String,Object> parameters)
	{
		StringBuilder buf = new StringBuilder();
		buf.append(collection.toString());
		if (path != null) {
			buf.append("/");
			buf.append(path);
		}
		if (parameters.size() > 0) {
			buf.append("?");
			buf.append(parameters.toString());   // sloppy!
		}
		return buf.toString();
	}

	private void cleanUpListing(DBObject[] listing, String idField) 
	{
		for (DBObject obj : listing) {
			if (!"id".equals(idField)) {
				obj.put("id", obj.get(idField));
				obj.removeField(idField);
			}
			obj.removeField("_id");
		}
	}

	private void sortListing(DBObject[] listing)
	{
		Arrays.sort(listing, new Comparator<DBObject>() {
			@Override
			public int compare(DBObject o1, DBObject o2) {
				return idOf(o1).compareTo(idOf(o2));
			}

			private String idOf(DBObject obj) {
				Object idValue = obj.get("id");
				return idValue == null ? "" : idValue.toString();
			}
		});
	}

	private String addPathFields(DBObject q, String path)
		throws IOException
	{
		String[] pathComps = path.split("\\/");

		int pcx = 0;
		for (; pcx < pathComps.length; ++pcx) {
			if (pcx >= pathFields.length) {
				throw new IOException(path + ": too many path components");
			}
			q.put(pathFields[pcx], pathComps[pcx]);
		}

		return pcx < pathFields.length ? pathFields[pcx] : null;
	}
}
