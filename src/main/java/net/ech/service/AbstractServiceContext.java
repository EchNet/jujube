package net.ech.service;

import java.io.IOException;
import javax.servlet.ServletException;
import net.ech.nio.*;

public abstract class AbstractServiceContext
	implements ServiceContext
{
	private String queryPath;
	private ItemHandle content;

	@Override
	public String getQueryPath()
	{
		return queryPath;
	}

	@Override
	public void setQueryPath(String queryPath)
	{
		this.queryPath = queryPath;
	}

	@Override
	public ItemHandle getContent()
	{
		return content;
	}

	@Override
	public void setContent(ItemHandle content)
	{
		this.content = content;
	}

	public void handleResponseContent()
		throws IOException
	{
		if (getContent() != null) {

			Metadata metadata = getContent().getMetadata();
			String mimeType = metadata == null ? null : metadata.getMimeType();
			if (mimeType != null) {
				getResponse().setContentType(mimeType);
			}

			if (mimeType == null || !MimeType.getMimeType(mimeType).isText()) {

				BinaryPump.Config config = new BinaryPump.Config();
				config.setInputStream(getContent().openInputStream());
				config.addOutputStream(getResponse().getOutputStream());
				config.setErrorLog(new ErrorLog());
				BinaryPump pump = new BinaryPump(config);
				pump.run();
				getResponse().getOutputStream().flush();
			}
			else {

				String characterEncoding = metadata == null ? null : metadata.getCharacterEncoding();
				if (characterEncoding != null) {
					getResponse().setCharacterEncoding(characterEncoding);
				}

				TextPump.Config config = new TextPump.Config();
				config.setReader(getContent().openReader());
				config.addWriter(getResponse().getWriter());
				config.setErrorLog(new ErrorLog());
				TextPump pump = new TextPump(config);
				pump.run();
				getResponse().getWriter().flush();
			}
		}
	}

	public void sendError(int statusCode, String message)
	{
		try {
			getResponse().sendError(statusCode, message);
		}
		catch (IOException ignore) {
		}
	}
}
