package net.ech.util;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class TypeCoercionTest
{
	@Test
	public void testNegativeIntToMap() throws Exception
	{
		try {
			BeanPropertyMapSupport.coerce(Map.class, 5);
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testPositiveStringToChar() throws Exception
	{
		assertEquals(new Character('a'), BeanPropertyMapSupport.coerce(char.class, "a"));
	}

	@Test
	public void testPositiveStringToCharacter() throws Exception
	{
		assertEquals(new Character('b'), BeanPropertyMapSupport.coerce(Character.class, "b"));
	}

	@Test
	public void testStringTooLongToCoerceToChar() throws Exception
	{
		try {
			BeanPropertyMapSupport.coerce(char.class, "abc");
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testPositiveIntToChar() throws Exception
	{
		assertEquals(new Character('a'), BeanPropertyMapSupport.coerce(char.class, new Integer((int)'a')));
	}

	@Test
	public void testNegativeNegativeIntToChar() throws Exception
	{
		try {
			BeanPropertyMapSupport.coerce(char.class, new Integer(-1));
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testNegativeBigIntToChar() throws Exception
	{
		try {
			BeanPropertyMapSupport.coerce(char.class, new Integer(1000000000));
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testPositiveListToArray() throws Exception
	{
		List<Integer> originalList = new ArrayList<Integer>();
		originalList.add(6);
		originalList.add(7);
		originalList.add(8);
		Object coerced = BeanPropertyMapSupport.coerce(int[].class, originalList);
		assertNotNull(coerced);
		assertTrue(coerced.getClass().isArray());
		for (int i = 0; i < originalList.size(); ++i) {
			assertEquals(originalList.get(i).intValue(), ((int[]) coerced)[i]);
		}
	}

	@Test
	public void testNegativeListToArray() throws Exception
	{
		List<Object> originalList = new ArrayList<Object>();
		originalList.add(6);
		originalList.add("uh oh");
		originalList.add(8);
		try {
			BeanPropertyMapSupport.coerce(int[].class, originalList);
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testNegativeScalarToArray() throws Exception
	{
		try {
			BeanPropertyMapSupport.coerce(int[].class, 84);
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testPositiveListToSet() throws Exception
	{
		List<String> original = Arrays.asList(new String[] { "do", "re", "mi", "fa", "so", "la", "ti", "do" });
		Object coerced = BeanPropertyMapSupport.coerce(Set.class, original);
		assertTrue(coerced instanceof Set);
		assertEquals(7, ((Set) coerced).size());
		assertTrue(((Set) coerced).contains("re"));
	}
}
