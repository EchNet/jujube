package net.ech.config;

// See ConfigurationDescriptorTest.java

public class NotConfigurableTooManyConst
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

	public NotConfigurableTooManyConst()
	{
	}

	public NotConfigurableTooManyConst(Config config)
	{
		this.property = config.property;
	}

	public String getProperty()
	{
		return property;
	}
}
