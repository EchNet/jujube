package net.ech.nio;

import java.util.*;

public class MimeType
{
    public final static String PLAIN_TEXT = "text/plain";
    public final static String HTML = "text/html";
    public final static String JSON = "application/json";
    public final static String JAVASCRIPT = "application/x-javascript";
    public final static String FORM = "application/x-www-form-urlencoded";
    public final static String CSV = "text/csv";
    public final static String XML = "application/xml";

	private final static int A_TEXT = 0x1;
	private final static int A_JAVASCRIPT = 0x2;

	private static Map<String,Integer> mimeAttrs = new HashMap<String,Integer>();
	static {
		mimeAttrs.put(PLAIN_TEXT, A_TEXT);
		mimeAttrs.put(HTML, A_TEXT);
		mimeAttrs.put(JSON, A_TEXT | A_JAVASCRIPT);
		mimeAttrs.put(JAVASCRIPT, A_TEXT | A_JAVASCRIPT);
		mimeAttrs.put("application/javascript", A_TEXT | A_JAVASCRIPT);
		mimeAttrs.put(FORM, A_TEXT);
		mimeAttrs.put(CSV, A_TEXT);
	}

	public static MimeType getMimeType(String mime)
	{
		return new MimeType(mime, mime != null && mimeAttrs.containsKey(mime) ? mimeAttrs.get(mime).intValue() : 0);
	}

	private String mime;
	private int attrs;

	private MimeType(String mime, int attrs)
	{
		this.mime = mime;
		this.attrs = attrs;
	}

	public boolean isText()
	{
		return (attrs & A_TEXT) != 0;
	}
	
	public boolean isJavascript()
	{
		return (attrs & A_JAVASCRIPT) != 0;
	}
}
