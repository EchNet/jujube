package net.ech.service;

import net.ech.nio.*;
import java.io.*;
import javax.servlet.http.*;

public class ContentServiceModule
	extends AbstractServiceModule
	implements ServiceModule
{
	public ContentServiceModule(ServiceContext serviceContext)
	{
		super(serviceContext);
	}

	@Override
	public void contentReady()
	{
		String mimeType = getMimeType();
		if (mimeType != null) {
			getResponse().setContentType(mimeType);
		}

		try {
			if (isText(mimeType)) {

				String characterEncoding = getCharacterEncoding();
				if (characterEncoding != null) {
					getResponse().setCharacterEncoding(characterEncoding);
				}
				transferText();
			}
			else {
				transferBinary();
			}
		}
		catch (IOException e) {
		}
	}

	private String getMimeType()
	{
		Metadata metadata = getContentItemHandle().getMetadata();
		return metadata == null ? null : metadata.getMimeType();
	}

	private String getCharacterEncoding()
	{
		Metadata metadata = getContentItemHandle().getMetadata();
		return metadata == null ? null : metadata.getCharacterEncoding();
	}

	private boolean isText(String mimeType)
	{
		return false;
	}

	private void transferText()
		throws IOException
	{
		TextPump.Config config = new TextPump.Config();
		config.setReader(getContentItemHandle().openReader());
		config.addWriter(getResponse().getWriter());
		TextPump pump = new TextPump(config);
		pump.run();
		getResponse().getWriter().flush();
	}

	private void transferBinary()
		throws IOException
	{
		BinaryPump.Config config = new BinaryPump.Config();
		config.setInputStream(getContentItemHandle().openInputStream());
		config.addOutputStream(getResponse().getOutputStream());
		BinaryPump pump = new BinaryPump(config);
		pump.run();
		getResponse().getOutputStream().flush();
	}
}
