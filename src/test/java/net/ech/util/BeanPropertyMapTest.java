package net.ech.util;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class BeanPropertyMapTest
{
	@Test
	public void testPut() throws Exception
	{
		BeanType1 bean = new BeanType1();
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertNull(map.put("a", "1"));
		assertEquals("1", bean.getA());
		assertEquals("1", map.put("a", "2"));
		assertEquals("2", bean.getA());
		assertEquals("2", map.put("a", null));
		assertNull(bean.getA());
	}

	@Test
	public void testPutNoSuchProperty() throws Exception
	{
		try {
			BeanType1 bean = new BeanType1();
			Map<String,Object> map = new BeanPropertyMap(bean);
			map.put("d", "1");
		}
		catch (BeanException e) {
			assertEquals(BeanType1.class, e.getBeanClass());
			assertEquals("net.ech.util.BeanPropertyMapTest$BeanType1.d: no such property", e.getMessage());
		}
	}

	@Test
	public void testPutButMissingSetter() throws Exception
	{
		try {
			BeanType1 bean = new BeanType1();
			Map<String,Object> map = new BeanPropertyMap(bean);
			map.put("b", "1");
			fail("should not be reached");
		}
		catch (BeanException e) {
			assertEquals(BeanType1.class, e.getBeanClass());
			assertEquals("net.ech.util.BeanPropertyMapTest$BeanType1.b: no setter", e.getMessage());
		}
	}

	@Test
	public void testPutWithMissingGetter() throws Exception
	{
		BeanType1 bean = new BeanType1();
		Map<String,Object> map = new BeanPropertyMap(bean);
		map.put("c", "1");
		assertNull(map.put("c", "1"));
	}

	@Test
	public void testGet() throws Exception
	{
		BeanType1 bean = new BeanType1();
		bean.setA("1");
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertEquals("1", map.get("a"));
	}

	@Test
	public void testGetNoSuchProperty() throws Exception
	{
		BeanType1 bean = new BeanType1();
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertNull(map.get("d"));
	}

	@Test
	public void testGetButMissingGetter() throws Exception
	{
		try {
			BeanType1 bean = new BeanType1();
			Map<String,Object> map = new BeanPropertyMap(bean);
			map.get("c");
			fail("should not be reached");
		}
		catch (BeanException e) {
			assertEquals(BeanType1.class, e.getBeanClass());
			assertEquals("net.ech.util.BeanPropertyMapTest$BeanType1.c: no getter", e.getMessage());
		}
	}

	@Test
	public void testContainsKey() throws Exception
	{
		BeanType1 bean = new BeanType1();
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertTrue(map.containsKey("a"));
		assertTrue(map.containsKey("b"));
		assertTrue(map.containsKey("c"));
		assertFalse(map.containsKey("d"));
	}

	@Test
	public void testSize() throws Exception
	{
		assertEquals(3, new BeanPropertyMap(new BeanType1()).size());
	}

	@Test
	public void testSetThruIterator() throws Exception
	{
		BeanType1 bean = new BeanType1();
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertNull(bean.getA());
		for (Map.Entry<String,Object> entry : map.entrySet()) {
			if (entry.getKey().equals("a")) {
				entry.setValue("1");
			}
		}
		assertEquals("1", bean.getA());
	}

	@Test
	public void testRemoveUnsupported() throws Exception
	{
		BeanType1 bean = new BeanType1();
		Map<String,Object> map = new BeanPropertyMap(bean);
		Iterator<Map.Entry<String,Object>> iterator = map.entrySet().iterator();
		assertTrue(iterator.hasNext());
		iterator.next();
		try {
			iterator.remove();
			fail("should not be reached");
		}
		catch (UnsupportedOperationException e) {
		}
	}

	@Test
	public void testPutException() throws Exception
	{
		BeanType2 bean = new BeanType2();
		Map<String,Object> map = new BeanPropertyMap(bean);
		try {
			map.put("characterProp", 47);
		}
		catch (BeanException e) {
			assertEquals(BeanType2.class, e.getBeanClass());
			assertEquals("public void net.ech.util.BeanPropertyMapTest$BeanType2.setCharacterProp(java.lang.Character)", e.getMessage());
		}
	}

	@Test
	public void testGetException() throws Exception
	{
		BeanType3 bean = new BeanType3();
		Map<String,Object> map = new BeanPropertyMap(bean);
		try {
			map.get("a");
		}
		catch (BeanException e) {
			assertEquals(BeanType3.class, e.getBeanClass());
			assertEquals("public java.lang.String net.ech.util.BeanPropertyMapTest$BeanType3.getA()", e.getMessage());
		}
	}

	@Test
	public void testStringToCharacterCoercion() throws Exception
	{
		BeanType2 bean = new BeanType2();
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertNull(bean.characterProp);
		map.put("characterProp", "a");
		assertEquals(new Character('a'), bean.characterProp);
	}

	@Test
	public void testStringToCharCoercion() throws Exception
	{
		BeanType2 bean = new BeanType2();
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertEquals(0, bean.charProp);
		map.put("charProp", "a");
		assertEquals('a', bean.charProp);
	}

	@Test
	public void testStringToBoolCoercion() throws Exception
	{
		BeanType2 bean = new BeanType2();
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertFalse(bean.boolProp);
		map.put("boolProp", "true");
		assertTrue(bean.boolProp);
	}

	@Test
	public void testBooleanToBoolCoercion() throws Exception
	{
		BeanType2 bean = new BeanType2();
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertFalse(bean.boolProp);
		map.put("boolProp", new Boolean(true));
		assertTrue(bean.boolProp);
	}

	@Test
	public void testListToArrayCoercion() throws Exception
	{
		BeanType2 bean = new BeanType2();
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertNull(bean.arrayProp);
		map.put("arrayProp", Collections.singletonList("abc"));
		assertEquals(new String[]{ "abc" }, bean.arrayProp);
	}

	@Test
	public void testPolymorphListToArrayCoercion() throws Exception
	{
		BeanType2 bean = new BeanType2();
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertNull(bean.arrayProp);
		List<Number> nList = new ArrayList<Number>();
		nList.add(new Integer(1));
		nList.add(new Double(2.0));
		map.put("numberArrayProp", nList);
		assertEquals(new Number[]{ new Integer(1), new Double(2.0) }, bean.nArrayProp);
	}

	@Test
	public void testArrayToListCoercion() throws Exception
	{
		BeanType2 bean = new BeanType2();
		Map<String,Object> map = new BeanPropertyMap(bean);
		assertNull(bean.listProp);
		map.put("listProp", new String[]{ "abc" });
		assertEquals(Collections.singletonList("abc"), bean.listProp);
	}

	private static class BeanType1
	{
		private String a;

		public String getA() { return a; }
		public void setA(String a) { this.a = a; }

		public String getB() { return "b"; }

		public void setC(String c) {}
	}

	private static class BeanType2
	{
		Character characterProp;
		char charProp;
		String[] arrayProp;
		Number[] nArrayProp;
		List<String> listProp;
		boolean boolProp;

		public void setBoolProp(boolean boolProp) { this.boolProp = boolProp; }
		public boolean getBoolProp() { return boolProp; }
		public void setCharacterProp(Character characterProp) { this.characterProp = characterProp; }
		public Character getCharacterProp() { return characterProp; }
		public void setCharProp(char charProp) { this.charProp = charProp; }
		public char getCharProp() { return charProp; }
		public void setArrayProp(String[] arrayProp) { this.arrayProp = arrayProp; }
		public String[] getArrayProp() { return arrayProp; }
		public void setNumberArrayProp(Number[] nArrayProp) { this.nArrayProp = nArrayProp; }
		public Number[] getNumberArrayProp() { return nArrayProp; }
		public void setListProp(List<String> listProp) { this.listProp = listProp; }
		public List<String> getListProp() { return listProp; }
	}

	private static class BeanType3
	{
		public String getA() { throw new RuntimeException("foo"); }
	}
}
