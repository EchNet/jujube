package net.ech.config;

// See ConfigurationDescriptorTest.java

public class NotConfigurableConfigIsEnum
{
	private String property;

	public static enum Config
	{
		ONE, TWO, THREE
	}

	public NotConfigurableConfigIsEnum(Config config)
	{
	}

	public String getProperty()
	{
		return property;
	}
}
