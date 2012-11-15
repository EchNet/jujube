package net.ech.config;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ConfigurationDescriptorTest
{
	@Test
	public void testPositive() throws Exception
	{
		ConfigurationDescriptor cDesc = ConfigurationDescriptor.analyze(SimpleConfigurable.class);
		assertNotNull(cDesc);
		assertEquals(SimpleConfigurable.class, cDesc.getTargetClass());
		assertEquals(SimpleConfigurable.Config.class, cDesc.getConfiguratorClass());
		assertNotNull(cDesc.getConstructor());
	}

	@Test
	public void testTooFewConstructors() throws Exception
	{
		assertNull(ConfigurationDescriptor.analyze(Interface.class));
	}

	@Test
	public void testTooManyConstructors() throws Exception
	{
		assertNull(ConfigurationDescriptor.analyze(NotConfigurableTooManyConst.class));
	}

	@Test
	public void testConstTooManyParams() throws Exception
	{
		assertNull(ConfigurationDescriptor.analyze(NotConfigurableConfigConstParams.class));
	}

	@Test
	public void testConfiguratorIsInterface() throws Exception
	{
		assertNull(ConfigurationDescriptor.analyze(NotConfigurableConfigIsInterface.class));
	}

	@Test
	public void testConfiguratorIsEnum() throws Exception
	{
		assertNull(ConfigurationDescriptor.analyze(NotConfigurableConfigIsEnum.class));
	}

	@Test
	public void testConfiguratorIsNotMember() throws Exception
	{
		assertNull(ConfigurationDescriptor.analyze(NotConfigurableConfigIsNotMember.class));
	}

	private static interface Interface
	{
	}

	private static class NotConfigurableConfigIsNotMember
	{
		public NotConfigurableConfigIsNotMember(java.net.URL url)
		{
		}
	}
}
