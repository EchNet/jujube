package net.ech.service;

import java.io.IOException;
import javax.servlet.ServletException;
import net.ech.nio.ItemHandle;

public interface ServiceModule
{
	public void setServiceContext(ServiceContext context);

	/**
	 * Called before content item is available.  May make content item available.
	 */
	public void preprocess()
		throws IOException, ServletException;

	/**
	 * Called when content item (if any) is available.
	 */
	public void postprocess(ItemHandle itemHandle)
		throws IOException, ServletException;
}
