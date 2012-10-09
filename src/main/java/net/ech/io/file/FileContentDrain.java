package net.ech.io.file;

import net.ech.io.*;
import java.io.*;

public class FileContentDrain
	extends AbstractContentDrain
	implements ContentDrain
{
	private String path;

	public FileContentDrain(String path)
	{
		this.path = path;
	}

	/**
	 * Dump content to a file.
	 */
	@Override
	synchronized public ContentHandle accept(ContentHandle contentHandle)
		throws IOException
	{
		Writer writer = new BufferedWriter(new FileWriter(path));
		try {
			contentHandle.write(writer);
			writer.write("\n");
			writer.flush();
			return super.accept(contentHandle);
		}
		finally {
			writer.close();
		}
	}
}
