package net.ech.doc;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Stack;

public class DocumentCopier
	extends DocumentWalker
{
	private Object copy;
	private Stack<State> stateStack;

	public DocumentCopier(Document document)
	{
		super(document);
	}

	public DocumentCopier walk()
	{
		this.copy = null;
		this.stateStack = new Stack<State>();
		super.walk();
		return this;
	}

	public Document getCopy()
	{
		return new Document(copy);
	}

	@Override
	protected State initialState()
	{
		return new InitialState();
	}

	private class BaseState
		extends State
	{
		private boolean closeable;

		BaseState(boolean closeable) {
			this.closeable = closeable;
		}

		@Override
		public State close() {
			return closeable ? stateStack.pop() : super.close();
		}
	}

	private abstract class ValueState
		extends BaseState
	{
		ValueState(boolean closeable) {
			super(closeable);
		}

		@Override
		public State visitNull() {
			acceptValue(null);
			return postValueState();
		}

		@Override
		public State visitScalar(Object value) {
			acceptValue(value);
			return postValueState();
		}

		@Override
		public State visitDate(Date date) {
			acceptValue(new Date(date.getTime()));
			return postValueState();
		}

		@Override
		public State openList(int size) {
			ListState listState = new ListState(size);
			acceptValue(listState.list);
			stateStack.push(postValueState());
			return listState;
		}

		@Override
		public State openMap(int size) {
			MapState mapState = new MapState();
			acceptValue(mapState.map);
			stateStack.push(postValueState());
			return mapState;
		}

		abstract protected void acceptValue(Object value);
		abstract protected State postValueState();
	}

	private class InitialState
		extends ValueState
	{
		InitialState() {
			super(false);
		}

		@Override
		protected void acceptValue(Object value) {
			copy = value;
		}

		@Override
		protected State postValueState() {
			return new State() {};   // terminal
		}
	}

	private class ListState
		extends ValueState
	{
		List<Object> list;

		public ListState(int size) {
			super(true);
			list = new ArrayList<Object>(size);
		}

		@Override
		protected void acceptValue(Object value) {
			list.add(value);
		}

		protected State postValueState() {
			return this;
		}
	}

	private class MapState
		extends BaseState
	{
		Map<String,Object> map = new HashMap<String,Object>();

		public MapState() {
			super(true);
		}

		@Override
		public State openMapEntry(String key) {
			stateStack.push(this);
			return new MapEntryState(map, key);
		}
	}

	private class MapEntryState
		extends ValueState
	{
		Map<String,Object> map;
		String key;

		public MapEntryState(Map<String,Object> map, String key) {
			super(false);
			this.map = map;
			this.key = key;
		}

		@Override
		protected void acceptValue(Object value) {
			map.put(key, value);
		}

		@Override
		protected State postValueState() {
			return new BaseState(true);   // now you may only close
		}
	}
}
