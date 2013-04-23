package net.ech.doc;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Date;

public abstract class DocumentWalker
{
	private Document document;
	private State state;

	public DocumentWalker(Document document)
	{
		this.document = document;
	}

	public DocumentWalker walk()
	{
		state = initialState();
		walk(document.get());
		return this;
	}

	public static abstract class State
	{
		public State openList(int size) {
			throw new IllegalStateException();
		}
		public State openMap(int size) {
			throw new IllegalStateException();
		}
		public State openMapEntry(String key) {
			throw new IllegalStateException();
		}
		public State close() {
			throw new IllegalStateException();
		}
		public State visitScalar(Object stuff) {
			throw new IllegalStateException();
		}
		public State visitNull() {
			throw new IllegalStateException();
		}
		public State visitDate(Date date) {
			throw new IllegalStateException();
		}
	}

	abstract protected State initialState();

	private void walk(Object stuff)
	{
		if (stuff == null) {
			state = state.visitNull();
		}
		else if (stuff instanceof Map) {
			Map<String,Object> map = (Map<String,Object>) stuff;
			state = state.openMap(map.size());
            for (Map.Entry<String,Object> entry : map.entrySet()) {
				state = state.openMapEntry(entry.getKey());
				walk(entry.getValue());
				state = state.close();
            }
			state = state.close();
		}
		else if (stuff instanceof List) {
			List<Object> list = (List<Object>) stuff;
			state = state.openList(list.size());
            for (int i = 0; i < list.size(); ++i) {
				walk(list.get(i));
            }
			state = state.close();
		}
		else if (stuff.getClass().isArray()) {
			int length = Array.getLength(stuff);
			state = state.openList(length);
			Object newArray = Array.newInstance(stuff.getClass().getComponentType(), length);
			for (int i = 0; i < length; ++i) {
				walk(Array.get(stuff, i));
			}
			state = state.close();
		}
		else if (stuff instanceof Date) {
			state = state.visitDate((Date) stuff);
		}
		else if ((stuff instanceof String) ||
				(stuff instanceof Number) ||
				(stuff instanceof Boolean)) {
			state = state.visitScalar(stuff);
		}
		else {
			throw new IllegalArgumentException(stuff + ": " + stuff.getClass() + ": bad document node type");
		}
	}
}
