package net.ech.service;

import java.io.IOException;
import javax.servlet.ServletException;
import net.ech.nio.ItemHandle;

public interface ServiceModule
{
	/**
	 * Initialize this ServiceModule.
	 */
	public void setServiceContext(ServiceContext context);
	public void setModulePath(String modulePath);
	public void setQueryPath(String queryPath);

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
