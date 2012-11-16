package net.ech.service;

import net.ech.nio.*;
import java.io.*;
import javax.servlet.http.*;

public class ContentServiceModule
	extends AbstractServiceModule
	implements ServiceModule
{
	@Override
	public void postprocess(ItemHandle contentItemHandle)
	{
		String mimeType = getMimeType(contentItemHandle);
		if (mimeType != null) {
			getResponse().setContentType(mimeType);
		}

		try {
			if (isText(mimeType)) {

				String characterEncoding = getCharacterEncoding(contentItemHandle);
				if (characterEncoding != null) {
					getResponse().setCharacterEncoding(characterEncoding);
				}
				transferText(contentItemHandle);
			}
			else {
				transferBinary(contentItemHandle);
			}
		}
		catch (IOException e) {
		}
	}

	private String getMimeType(ItemHandle contentItemHandle)
	{
		Metadata metadata = contentItemHandle.getMetadata();
		return metadata == null ? null : metadata.getMimeType();
	}

	private String getCharacterEncoding(ItemHandle contentItemHandle)
	{
		Metadata metadata = contentItemHandle.getMetadata();
		return metadata == null ? null : metadata.getCharacterEncoding();
	}

	private boolean isText(String mimeType)
	{
		return false;
	}

	private void transferText(ItemHandle contentItemHandle)
		throws IOException
	{
		TextPump.Config config = new TextPump.Config();
		config.setReader(contentItemHandle.openReader());
		config.addWriter(getResponse().getWriter());
		config.setErrorLog(new ErrorLog());
		TextPump pump = new TextPump(config);
		pump.run();
		getResponse().getWriter().flush();
	}

	private void transferBinary(ItemHandle contentItemHandle)
		throws IOException
	{
		BinaryPump.Config config = new BinaryPump.Config();
		config.setInputStream(contentItemHandle.openInputStream());
		config.addOutputStream(getResponse().getOutputStream());
		config.setErrorLog(new ErrorLog());
		BinaryPump pump = new BinaryPump(config);
		pump.run();
		getResponse().getOutputStream().flush();
	}
}
