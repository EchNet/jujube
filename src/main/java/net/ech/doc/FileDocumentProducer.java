package net.ech.doc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;

public class FileDocumentProducer
	extends StreamDocumentProducer
	implements DocumentProducer
{
	private String fileName;

	public FileDocumentProducer(String fileName)
	{
		this("file:" + fileName, fileName);
	}

	public FileDocumentProducer(String source, String fileName)
	{
		super(source);
		this.fileName = fileName;
	}

	@Override
	protected Reader openReader()
		throws IOException
	{
		return new BufferedReader(new FileReader(fileName));
	}
}
