package net.ech.config;

// See ConfigurationDescriptorTest.java

public class NotConfigurableConfigIsInterface
{
	private String property;

	public static interface Config
	{
		public void setProperty(String property);
	}

	public NotConfigurableConfigIsInterface(Config config)
	{
	}

	public String getProperty()
	{
		return property;
	}
}
