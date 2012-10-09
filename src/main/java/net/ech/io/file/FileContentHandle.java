package net.ech.io.file;

import net.ech.codec.*;
import net.ech.io.*;
import java.io.*;

/**
 * Treat a File as a ContentHandle.
 */
public class FileContentHandle
	extends AbstractContentHandle
    implements ContentHandle
{
	private File file;
	private Codec codec;

	/**
	 * Constructor.
	 */
	public FileContentHandle(File file, Codec codec)
		throws IOException
	{
		super(file.toString());
		this.file = file;
		this.codec = codec;
	}

	public File getFile()
	{
		return file;
	}

	@Override
	public String getVersion()
	{
		return Long.toString(file.lastModified(), 16);
	}

	@Override
	public Codec getCodec()
		throws IOException
	{
		return codec;
	}

	@Override
    public Object getDocument()
        throws IOException
	{
		InputStream inputStream = new FileInputStream(file);
		try {
			return codec.decode(inputStream);
		}
		finally {
			inputStream.close();
		}
	}

	/**
	 * Stream output from File connection, bypassing unnecessary decoding and encoding steps.
	 */
	@Override
    public void write(OutputStream outputStream)
        throws IOException
	{
		InputStream inputStream = new FileInputStream(file);
		try {
			AbstractCodec.transferBytes(inputStream, outputStream);
		}
		finally {
			inputStream.close();
		}
	}

	/**
	 * Stream output from File connection to character output stream.
	 */
	@Override
    public void write(Writer writer)
        throws IOException
	{
		InputStream inputStream = new FileInputStream(file);
		try {
			codec.write(inputStream, writer);
		}
		finally {
			inputStream.close();
		}
	}
}
