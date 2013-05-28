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
			TypeCoercionSupport.coerce(Map.class, 5);
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testPositiveDoubleToInt() throws Exception
	{
		assertEquals(new Integer(-1000007), TypeCoercionSupport.coerce(int.class, -1000007.0));
	}

	@Test
	public void testNegativeDoubleToInt() throws Exception
	{
		try {
			TypeCoercionSupport.coerce(int.class, 0.232);
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testPositiveLongToInt() throws Exception
	{
		assertEquals(new Integer(-1000007), TypeCoercionSupport.coerce(int.class, -1000007L));
	}

	@Test
	public void testNegativeLongToInt() throws Exception
	{
		try {
			TypeCoercionSupport.coerce(int.class, 10000000000000L);
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testPositiveStringToInt() throws Exception
	{
		assertEquals(new Integer(123), TypeCoercionSupport.coerce(int.class, "123"));
	}

	@Test
	public void testNegativeStringToInt() throws Exception
	{
		try {
			TypeCoercionSupport.coerce(int.class, "a1&a2&a3");
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testNegativeStringToDouble() throws Exception
	{
		try {
			TypeCoercionSupport.coerce(double.class, "123");
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testPositiveFloatToDouble() throws Exception
	{
		assertEquals(3.03, ((Double)TypeCoercionSupport.coerce(double.class, 3.03F)).doubleValue(), 0.001);
	}

	@Test
	public void testPositiveIntToDouble() throws Exception
	{
		assertEquals(new Double(3), TypeCoercionSupport.coerce(Double.class, 3));
	}

	@Test
	public void testPositiveStringToChar() throws Exception
	{
		assertEquals(new Character('a'), TypeCoercionSupport.coerce(char.class, "a"));
	}

	@Test
	public void testPositiveStringToCharacter() throws Exception
	{
		assertEquals(new Character('b'), TypeCoercionSupport.coerce(Character.class, "b"));
	}

	@Test
	public void testStringTooLongToCoerceToChar() throws Exception
	{
		try {
			TypeCoercionSupport.coerce(char.class, "abc");
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testPositiveIntToChar() throws Exception
	{
		assertEquals(new Character('a'), TypeCoercionSupport.coerce(char.class, new Integer((int)'a')));
	}

	@Test
	public void testNegativeNegativeIntToChar() throws Exception
	{
		try {
			TypeCoercionSupport.coerce(char.class, new Integer(-1));
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testNegativeBigIntToChar() throws Exception
	{
		try {
			TypeCoercionSupport.coerce(char.class, new Integer(1000000000));
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
		Object coerced = TypeCoercionSupport.coerce(int[].class, originalList);
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
			TypeCoercionSupport.coerce(int[].class, originalList);
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testNegativeScalarToArray() throws Exception
	{
		try {
			TypeCoercionSupport.coerce(int[].class, 84);
			fail("should not be reached");
		}
		catch (TypeMismatchException e) {
		}
	}

	@Test
	public void testPositiveListToSet() throws Exception
	{
		List<String> original = Arrays.asList(new String[] { "do", "re", "mi", "fa", "so", "la", "ti", "do" });
		Object coerced = TypeCoercionSupport.coerce(Set.class, original);
		assertTrue(coerced instanceof Set);
		assertEquals(7, ((Set) coerced).size());
		assertTrue(((Set) coerced).contains("re"));
	}
}
