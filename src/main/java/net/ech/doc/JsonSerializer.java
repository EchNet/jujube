package net.ech.doc;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * A really simple JSON serializer.
 */
public class JsonSerializer
{
	private Map<Class,Formatter> formatters = new HashMap<Class,Formatter>();

	public static interface Serializer
	{
		public void serialize(Object obj) throws IOException;
		public Writer getWriter();
	}

	public static interface Formatter
	{
		public void write(Object obj, Serializer serializer) throws IOException;
	}

	public JsonSerializer()
	{
		initFormatters();
	}

	private void initFormatters()
	{
		formatters.put(Map.class, new MapFormatter());
		formatters.put(List.class, new ListFormatter());
		formatters.put(Character.class, new StringFormatter());
		formatters.put(String.class, new StringFormatter());
	}

	public String encode(Object obj)
		throws IOException
	{
		StringWriter buf = new StringWriter();
		write(obj, buf);
		return buf.toString();
	}

	public void write (Object obj, final Writer writer)
		throws IOException
	{
		new Serializer()
		{
			public void serialize(Object obj) throws IOException
			{
				if (obj == null) {
					writer.write("null");
				}
				else {
					getFormatter(obj).write(obj, this);
				}
			}

			public Writer getWriter() {
				return writer;
			}
		}.serialize(obj);
	}

	private Formatter getFormatter(Object obj)
	{
		Formatter formatter = formatters.get(obj.getClass());
		if (formatter == null) {
			for (Map.Entry<Class,Formatter> entry : formatters.entrySet()) {
				if (entry.getKey().isInstance(obj)) {
					formatter = entry.getValue();
				}
			}
			if (formatter == null) {
				formatter = obj.getClass().getName().startsWith("[") ? new ArrayFormatter()
																	 : new DefaultFormatter();
			}
			formatters.put(obj.getClass(), formatter);
		}

		return formatter;
	}

	private class DefaultFormatter implements Formatter
	{
		public void write(Object obj, Serializer serializer) throws IOException {
			serializer.getWriter().write(obj.toString());
		}
	}

	private class StringFormatter implements Formatter
	{
		public void write(Object obj, Serializer serializer) throws IOException {
			writeStringLiteral(obj.toString(), serializer.getWriter());
		}
	}

	/**
	 * Enable trapping of string literals.
	 */
	protected void writeStringLiteral(String value, Writer writer)
		throws IOException
	{
		writer.write("\"");
		escapeString(value, writer);
		writer.write("\"");
	}

	private class MapFormatter implements Formatter
	{
		public void write(Object map, Serializer serializer) throws IOException {
			serializer.getWriter().write("{");
			int propCount = 0;
			for (Map.Entry<?,?> entry : ((Map<?,?>)map).entrySet()) {
				if (propCount > 0) serializer.getWriter().write(",");
				String key = entry.getKey().toString();
				serializer.getWriter().write("\"");
				escapeString(key, serializer.getWriter());
				serializer.getWriter().write("\"");
				serializer.getWriter().write(":");
				serializer.serialize(entry.getValue());
				++propCount;
			}
			serializer.getWriter().write("}");
		}
	}

	private class ListFormatter implements Formatter
	{
		public void write(Object list, Serializer serializer) throws IOException
		{
			serializer.getWriter().write("[");
			int childCount = 0;
			for (Object child : (List<?>)list) {
				if (childCount > 0) serializer.getWriter().write(",");
				serializer.serialize(child);
				childCount += 1;
			}
			serializer.getWriter().write("]");
		}
	}

	private class ArrayFormatter implements Formatter
	{
		public void write(Object array, Serializer serializer) throws IOException {
			serializer.getWriter().write("[");
			int childCount = 0;
			int length = Array.getLength(array);
			for (int i = 0; i < length; ++i) {
				if (childCount > 0) serializer.getWriter().write(",");
				serializer.serialize(Array.get(array, i));
				childCount += 1;
			}
			serializer.getWriter().write("]");
		}
	}

	private void escapeString(String s, Writer writer) throws IOException
	{
		for(int i=0;i<s.length();i++){
			char ch=s.charAt(i);
			switch(ch){
			case '"':
				writer.write("\\\"");
				break;
			case '\\':
				writer.write("\\\\");
				break;
			case '\b':
				writer.write("\\b");
				break;
			case '\f':
				writer.write("\\f");
				break;
			case '\n':
				writer.write("\\n");
				break;
			case '\r':
				writer.write("\\r");
				break;
			case '\t':
				writer.write("\\t");
				break;
			case '/':
				if (i == 0 || i == s.length() - 1) {
					writer.write("\\");
				}
				writer.write("/");
				break;
			default:
				//Reference: http://www.unicode.org/versions/Unicode5.1.0/
				if((ch>='\u0000' && ch<='\u001F') || (ch>='\u007F' && ch<='\u009F') || (ch>='\u2000' && ch<='\u20FF')){
					String ss=Integer.toHexString(ch);
					writer.write("\\u");
					for(int k=0;k<4-ss.length();k++){
						writer.write('0');
					}
					writer.write(ss.toUpperCase());
				}
				else{
					writer.write(ch);
				}
			}
		}//for
	}
}
