package net.ech.config;

public class SimpleConfigurable
{
	private String property;

	public SimpleConfigurable()
	{
	}

	public SimpleConfigurable(SimpleConfigurable config)
	{
		this.property = config.property;
	}

	public String getProperty()
	{
		return property;
	}

	public void setProperty(String property)
	{
		this.property = property;
	}
}
