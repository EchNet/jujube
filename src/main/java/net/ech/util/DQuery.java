// Like jQuery, only...

package net.ech.util;

import java.io.IOException;
import java.util.*;
import java.lang.reflect.Array;

public class DQuery
{
    private Object stuff;
    private DPath path;
	private DQuery parent;
	private String documentName;

    public DQuery(Object stuff)
    {
		this(stuff, new DPath(), null);
    }

    public DQuery(Object stuff, String documentName)
    {
		this(stuff, new DPath().setDocumentName(documentName), null);
    }

    private DQuery(Object stuff, DPath path, DQuery parent)
    {
		this.stuff = stuff;
		this.path = path;
		this.parent = parent;
    }

    public Object get()
    {
        return stuff;
    }

    public DPath getPath()
    {
        return path;
    }

	public DQuery getParent()
	{
		return parent;
	}

    public boolean isNull()
    {
        return stuff == null;
    }

    public int getSize()
    {
        if (stuff instanceof List) {
            return ((List<Object>)stuff).size();
        }
        else if (stuff instanceof Map) {
            return ((Map<String,Object>)stuff).size();
        }
        else {
            return stuff == null ? 0 : 1;
        }
    }

    public DQuery rebase()
    {
        return new DQuery(stuff);
    }

    public DQuery find(String expression)
    {
        return find(DPath.parse(expression));
    }

    public DQuery find(DPath path)
    {
        DQuery next = this;
        for (Object pathComponent : path) {
			Object newStuff;
			DPath newPath;
			if (pathComponent instanceof Integer) {
				int index = ((Integer) pathComponent).intValue();
				newStuff = next.deref(index);
				newPath = new DPath(next.path).append(index);
			}
			else {
				String fieldName = pathComponent.toString();
				newStuff = next.deref(fieldName);
				newPath = new DPath(next.path).append(fieldName);
			}
			next = new DQuery(newStuff, newPath, next);
        }
        return next;
    }

    public void each(DHandler handler)
        throws IOException
    {
        if (stuff instanceof List) {
			for (int i = 0; i < ((List<Object>)stuff).size(); ++i) {
                handler.handle(new DQuery(deref(i), new DPath(path).append(i), this));
            }
        }
        else if (stuff instanceof Map) {
            for (String key : ((Map<String,Object>) stuff).keySet()) {
                handler.handle(new DQuery(deref(key), new DPath(path).append(key), this));
            }
        }
    }

	public DQuery copyDoc()
	{
		return copyDoc(new AbstractDFilter());
	}

	public DQuery copyDoc(DFilter filter)
	{
		if (stuff == null || (stuff instanceof String) || (stuff instanceof Number) || (stuff instanceof Boolean)) {
			return this;
		}
		if (stuff instanceof List) {
			List<Object> list = (List<Object>) stuff;
			List<Object> newList = new ArrayList<Object>();
            for (int i = 0; i < list.size(); ++i) {
				DQuery child = new DQuery(deref(i), new DPath(path).append(i), this);
				if (filter.preallow(child)) {
					child = child.copyDoc(filter);
					if (filter.postallow(child)) {
						newList.add(child.get());
					}
				}
            }
			return new DQuery(newList, this.path, this.parent);
		}
		if (stuff.getClass().isArray()) {
			int length = Array.getLength(stuff);
			Object newArray = Array.newInstance(stuff.getClass().getComponentType(), length);
			for (int i = 0; i < length; ++i) {
				Array.set(newArray, i, Array.get(stuff, i));
			}
			return new DQuery(newArray, this.path, this.parent);
		}
		if (stuff instanceof Map) {
			Hash newHash = new Hash();
            for (String key : ((Map<String,Object>) stuff).keySet()) {
				DQuery child = new DQuery(deref(key), new DPath(path).append(key), this);
				if (filter.preallow(child)) {
					child = child.copyDoc(filter);
					if (filter.postallow(child)) {
						newHash.addEntry(key, child.get());
					}
				}
            }
			return new DQuery(newHash, this.path, this.parent);
		}
		if (stuff instanceof Date) {
			return new DQuery(new Date(((Date) stuff).getTime()));
		}
		throw new IllegalArgumentException(stuff + ": " + stuff.getClass() + " not copyable");
	}

	public DQuery extend(DQuery operand)
	{
		if ((stuff instanceof Map) && (operand.stuff instanceof Map)) {
			//
			// Merge two maps into one.
			//
			Set<String> keys = new HashSet<String>();
			keys.addAll(((Map<String,Object>) stuff).keySet());
			keys.addAll(((Map<String,Object>) operand.stuff).keySet());
			Map<String,Object> resultMap = new HashMap<String,Object>();
			for (String key : keys) {
				DPath keyPath = new DPath(key);
				resultMap.put(key, find(keyPath).extend(operand.find(keyPath)).get());
			}
			return new DQuery(resultMap);
		}
		else if (operand.stuff != null) {
			//
			// In all other cases, favor the right hand side.  Handling of lists is an open issue.
			//
			return operand.copyDoc();
		}

		return copyDoc();
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

    public String toString()
    {
        return stuff == null ? "null" : stuff.toString();
    }

    private Object deref(String fieldName)
    {
		if (stuff instanceof Map) {
			return ((Map<String,Object>)stuff).get(fieldName);
		}

		if (stuff instanceof List) {
			return listField((List<Object>) stuff, fieldName);
		}

		return null;
    }

	private static List<Object> listField(List<Object> list, String fieldName)
	{
		List<Object> results = new ArrayList<Object>();
		for (Object item : list) {
			DQuery deref = new DQuery(item).find(fieldName);
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
