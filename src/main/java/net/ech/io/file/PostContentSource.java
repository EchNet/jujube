package net.ech.io.file;

import net.ech.codec.*;
import net.ech.io.*;
import net.ech.util.*;
import java.io.*;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;

//
// Museum code, might be useful someday.
//
public class PostContentSource
	extends AbstractContentSource
{
	public final String OFFERS = "Offers";

	@Override
	public ContentHandle resolve(ContentRequest request)
		throws IOException
	{
		String url = "some URL";
		HttpClient client = new DefaultHttpClient();
		HttpPost method = new HttpPost(url);
		method.setHeader("Content-type", "application/json; charset=UTF-8");
		method.setEntity(new StringEntity(request.getPath() + " " + request.getParameters()));
		HttpResponse response = client.execute(method);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new IOException(url + ": " + response.getStatusLine());
		}
		if (response.getEntity().getContentType() == null ||
			response.getEntity().getContentType().getValue() == null) {
			throw new IOException(url + ": no content type");
		}
		// getContentType() does not parse out the character encoding.
		if (!response.getEntity().getContentType().getValue().startsWith("application/json")) {
			throw new IOException(url + ": response not JSON");
		}
		return new JsonContentHandle(new JsonCodec().decode(response.getEntity().getContent()));
	}
}
