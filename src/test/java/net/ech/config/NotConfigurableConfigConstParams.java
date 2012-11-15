package net.ech.config;

// See ConfigurationDescriptorTest.java

public class NotConfigurableConfigConstParams
{
	private String property;

	public static class Config
	{
		private String property;

		public void setProperty(String property)
		{
			this.property = property;
		}
	}

	public NotConfigurableConfigConstParams(Config config, int x)
	{
		this.property = config.property;
	}

	public String getProperty()
	{
		return property;
	}
}
