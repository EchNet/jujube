package net.ech.util;

import java.util.*;

/**
 * A list of indexes representing the path to a node in a hierarchical data document.
 * Each element is either a String or an Integer.
 * Extends List<Object> so that no special JSON serialization code is required.
 */
public class DPath
	extends ArrayList<Object>
{
    private String documentName;

    public DPath()
    {
    }

    public DPath(String first)
    {
        add(first);
    }

    public DPath(Integer first)
    {
        add(first);
    }

    public DPath(DPath that)
    {
		super(that);
		this.documentName = that.documentName;
    }

	public DPath setDocumentName(String documentName)
	{
		this.documentName = documentName;
		return this;
	}

	public String getDocumentName()
	{
		return documentName;
	}

    public DPath append(String index)
    {
        add(index);
        return this;
    }

    public DPath append(Integer index)
    {
        add(index);
        return this;
    }

    public Object getLast()
    {
        return size() > 0 ? get(size() - 1) : null;
    }

    public DPath getParent()
    {
        DPath parent = new DPath(this);
		parent.remove(size() - 1);
        return parent;
    }

    public String getSignature()
    {
        StringBuilder buf = new StringBuilder();
        for (Object element : this) {
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append((element instanceof Integer) ? "*" : element.toString());
        }
        return buf.toString();
    }

	@Override
    public int hashCode()
    {
		int hash = super.hashCode();
		if (documentName != null) {
			hash = (hash * 37) | documentName.hashCode();
		}
		return hash;
	}

	@Override
    public boolean equals(Object that)
    {
		try {
			String thatDocumentName = ((DPath) that).documentName;
			return super.equals(that) &&
				((documentName == null) == (thatDocumentName == null)) &&
				(documentName == null || documentName.equals(thatDocumentName));
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
		if (documentName != null) {
			buf.append('{');
			buf.append(documentName);
			buf.append('}');
		}
        for (Object element : this) {
			if (element instanceof Number) { 
				buf.append('[');
				buf.append(element);
				buf.append(']');
			}
			else {
				if (buf.length() > 0) {
					buf.append('.');
				}
				buf.append(element);
			}
        }
        return buf.toString();
    }
}
