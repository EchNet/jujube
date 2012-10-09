package net.ech.io;

import net.ech.util.*;
import java.io.*;
import java.util.*;

public class AggregateContentSource
	extends AbstractContentSource
	implements ContentSource
{
	private List<ContentSource> children = new ArrayList<ContentSource>();

	public List<ContentSource> getChildren()
	{
		return children;
	}

	public void addChild(ContentSource source)
	{
		this.children.add(source);
	}

	@Override
	public Object[] list(String path)
		throws IOException
	{
		Map<String,Object> map = new HashMap<String,Object>();

		for (ContentSource child : children) {
			try {
				for (Object listing : child.list(path)) {
					String id = idOf(listing);
					if (!map.containsKey(id)) {
						map.put(id, listing);
					}
				}
			}
			catch (FileNotFoundException e) {
			}
		}

		Object[] array = map.values().toArray(new Object[0]);
		Arrays.sort(array, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				return idOf(o1).compareTo(idOf(o2));
			}

		});
		return array;
	}

	private String idOf(Object obj)
	{
		try {
			DQuery dq = new DQuery(obj);
			String str = dq.get(String.class);
			return str != null ? str : dq.find("id").cast(String.class, "");
		}
		catch (DocumentException e) {
			return "";
		}
	}
}
