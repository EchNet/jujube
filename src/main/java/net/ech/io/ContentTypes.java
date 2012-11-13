package net.ech.io;

import net.ech.codec.*;
import java.util.*;

public abstract class ContentTypes
{
    public final static String TEXT_CONTENT_TYPE = "text/plain";
    public final static String HTML_CONTENT_TYPE = "text/html";
    public final static String JSON_CONTENT_TYPE = "application/json";
    public final static String JAVASCRIPT_CONTENT_TYPE = "application/x-javascript";
    public final static String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public final static String CSV_CONTENT_TYPE = "text/csv";
    public final static String XML_CONTENT_TYPE = "application/xml";

	private static List<String> TEXT_CONTENT_TYPES = Arrays.asList(new String[] {
		TEXT_CONTENT_TYPE,
		HTML_CONTENT_TYPE,
		JSON_CONTENT_TYPE,
		JAVASCRIPT_CONTENT_TYPE,
		"application/javascript",
		FORM_CONTENT_TYPE,
		CSV_CONTENT_TYPE
	});

	public static boolean isForm(String contentType)
	{
		return FORM_CONTENT_TYPE.equals(contentType);
	}

	public static boolean isText(String contentType)
	{
		return TEXT_CONTENT_TYPES.contains(contentType);
	}
	
	public static boolean isJson(String contentType)
	{
		return JSON_CONTENT_TYPE.equals(contentType);
	}
	
	public static boolean isCsv(String contentType)
	{
		return CSV_CONTENT_TYPE.equals(contentType);
	}
	
	public static boolean isJavascript(String contentType)
	{
		return JAVASCRIPT_CONTENT_TYPE.equals(contentType) ||
			"application/javascript".equals(contentType);
	}

	public static Codec getDefaultCodec(String contentType)
	{
		return getDefaultCodec(contentType, null);
	}

	public static Codec getDefaultCodec(String contentType, String charSet)
	{
		if (contentType == null) {
			contentType = TEXT_CONTENT_TYPE;
		}
		if (isJson(contentType)) {
			return charSet == null ? new JsonCodec() : new JsonCodec(charSet);
		}
		if (isText(contentType)) {
			return charSet == null ? new TextCodec(contentType) : new TextCodec(contentType, charSet);
		}
		return new BinaryCodec(contentType);
	}
}
