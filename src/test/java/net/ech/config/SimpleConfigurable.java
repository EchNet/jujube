package net.ech.config;

// See ConfigurationDescriptorTest.java

public class SimpleConfigurable
{
	private String property;

	public static class Config
	{
		private String property;

		public String getProperty()
		{
			return property;
		}

		public void setProperty(String property)
		{
			this.property = property;
		}
	}

	public SimpleConfigurable(Config config)
	{
		this.property = config.property;
	}

	public String getProperty()
	{
		return property;
	}
}
