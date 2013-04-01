package net.ech.doc;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Array;

public class Document
{
    private Object stuff;
	private String source;
    private DocPath path;

    public Document(Object stuff)
    {
		this(stuff, "(in-memory document)", new DocPath());
    }

    public Document(Object stuff, String source)
    {
		this(stuff, source, new DocPath());
    }

    private Document(Object stuff, String source, DocPath path)
    {
		this.stuff = stuff;
		this.source = source;
		this.path = path;
    }

    public Object get()
    {
        return stuff;
    }

    public DocPath getPath()
    {
        return path == null ? new DocPath() : path;
    }

    public boolean isNull()
    {
        return stuff == null;
    }

    public Document find(String expression)
    {
        return find(DocPath.parse(expression));
    }

    public Document find(DocPath path)
    {
        Document next = this;
        for (Object pathComponent : path) {
			Object newStuff;
			DocPath newPath;
			if (pathComponent instanceof Integer) {
				int index = ((Integer) pathComponent).intValue();
				newStuff = next.deref(index);
				newPath = new DocPath(next.path).append(index);
			}
			else {
				String fieldName = pathComponent.toString();
				newStuff = next.deref(fieldName);
				newPath = new DocPath(next.path).append(fieldName);
			}
			next = new Document(newStuff, source, newPath);
        }
        return next;
    }

    public List<Document> children()
    {
		List<Document> children = new ArrayList<Document>();
        if (stuff instanceof List) {
			for (int i = 0; i < ((List<Object>)stuff).size(); ++i) {
                children.add(new Document(deref(i), source, new DocPath(path).append(i)));
            }
        }
        else if (stuff instanceof Map) {
            for (Map.Entry<Object,Object> entry : ((Map<Object,Object>) stuff).entrySet()) {
                children.add(new Document(entry.getValue(), source, new DocPath(path).append(entry.getKey())));
            }
        }
		return children;
    }

	public Document copy()
	{
		return new Document(copyStuff(stuff), this.source, this.path);
	}

	private static Object copyStuff(Object stuff)
	{
		if (stuff == null || (stuff instanceof String) || (stuff instanceof Number) || (stuff instanceof Boolean)) {
			return stuff;
		}
		if (stuff instanceof List) {
			List<Object> list = (List<Object>) stuff;
			List<Object> newList = new ArrayList<Object>();
            for (int i = 0; i < list.size(); ++i) {
				newList.add(copyStuff(list.get(i)));
            }
			return newList;
		}
		if (stuff.getClass().isArray()) {
			int length = Array.getLength(stuff);
			Object newArray = Array.newInstance(stuff.getClass().getComponentType(), length);
			for (int i = 0; i < length; ++i) {
				Array.set(newArray, i, Array.get(stuff, i));
			}
			return newArray;
		}
		if (stuff instanceof Map) {
			Map<Object,Object> newMap = new HashMap<Object,Object>();
            for (Map.Entry<Object,Object> entry : ((Map<Object,Object>) stuff).entrySet()) {
				newMap.put(entry.getKey(), copyStuff(entry.getValue()));
            }
			return newMap;
		}
		if (stuff instanceof Date) {
			return new Date(((Date) stuff).getTime());
		}
		throw new IllegalArgumentException(stuff + ": " + stuff.getClass() + " not copyable");
	}

	public Document extend(Document operand)
	{
		if ((stuff instanceof Map) && (operand.stuff instanceof Map)) {
			//
			// Merge two maps into one.
			//
			Set<String> keys = new HashSet<String>();
			keys.addAll(((Map<String,Object>) operand.stuff).keySet());
			keys.addAll(((Map<String,Object>) stuff).keySet());
			Map<String,Object> resultMap = new HashMap<String,Object>();
			for (String key : keys) {
				DocPath keyPath = new DocPath(key);
				resultMap.put(key, find(keyPath).extend(operand.find(keyPath)).get());
			}
			return new Document(resultMap);
		}

		//
		// In all other cases, favor the left hand side.  This includes lists for now.
		//
		return stuff == null ? operand.copy() : copy();
	}

	/**
	 * If the referenced item is of the given type, return the item; otherwise, return null.
	 */
	public <T> T get(Class<T> type)
	{
		if (stuff == null)
			return null;
		return doGet(type);
	}

	/**
	 * If the referenced item is of the given type, return the item.  If it is null, return the
	 * default value.  Otherwise, throw a DocumentException.
	 */
	public <T> T cast(Class<T> type, T dflt) throws DocumentException
	{
		if (stuff == null)
			return dflt;
		T obj = doGet(type);
		if (obj != null)
			return obj;
		throw new DocumentException(path + ": node must be empty or of type " + type.getName());
	}

	public <T> T require(Class<T> type) throws DocumentException
	{
		if (stuff == null)
			throw new DocumentException(path + ": missing node");
		T obj = doGet(type);
		if (obj != null)
			return obj;
		throw new DocumentException(path + ": node must be of type " + type.getName());
	}

	public String getSource()
	{
		StringBuilder buf = new StringBuilder();
		if (source != null) {
			buf.append(source);
		}
		if (path != null && path.size() > 0) {
			if (buf.length() > 0) {
				buf.append(":");
			}
			buf.append(path.toString());
		}
		return buf.toString();
	}

    public String toString()
    {
        return stuff == null ? "null" : stuff.toString();
    }

    private Object deref(Object fieldName)
    {
		if (stuff instanceof Map) {
			return ((Map<Object,Object>)stuff).get(fieldName);
		}

		if (stuff instanceof List) {
			return listField((List<Object>) stuff, (String)fieldName);
		}

		return null;
    }

	private static List<Object> listField(List<Object> list, String fieldName)
	{
		List<Object> results = new ArrayList<Object>();
		for (Object item : list) {
			Document deref = new Document(item).find(fieldName);
			if (!deref.isNull()) {
				results.add(deref.get());
			}
		}
		return results;
	}

    private Object deref(int index)
    {
		if (stuff instanceof List) {
			try {
				return ((List<Object>) stuff).get(index);
			}
			catch (ArrayIndexOutOfBoundsException e) {
			}
		}
		return null;
    }

	// Assumes that stuff is non-null.
	private <T> T doGet(Class<T> type)
	{
		if (type.isInstance(stuff))
			return type.cast(stuff);

		// Supported type coercions.
		if (type.equals(List.class) && stuff.getClass().isArray()) {
			return type.cast(Arrays.asList((Object[])stuff));
		}

		return null;
	}
}
