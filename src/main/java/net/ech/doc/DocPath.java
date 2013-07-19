package net.ech.doc;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * A list of indexes representing the path to a node in a hierarchical data document.
 * Each element is either a String or an Integer.
 * Extends List<Object> so that no special JSON serialization code is required.
 */
public class DocPath
	extends ArrayList<Object>
{
    /**
	 * 
	 */
	private static final long	serialVersionUID	= 4909490446567421568L;

	public static DocPath parse(String expression)
    {
		DocPath path = new DocPath();
		path.addAll(Arrays.asList(expression.split("\\.")));
        return path;
    }

    public DocPath()
    {
    }

    public DocPath(Object first)
    {
        add(first);
    }

    public DocPath(DocPath that)
    {
		super(that);
    }

    public DocPath append(Object index)
    {
        add(index);
        return this;
    }

    public Object getLast()
    {
        return size() > 0 ? get(size() - 1) : null;
    }

    public DocPath getParent()
    {
        DocPath parent = new DocPath(this);
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
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
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
