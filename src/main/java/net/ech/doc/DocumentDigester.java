package net.ech.doc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class DocumentDigester
	extends DocumentWalker
{
	private MessageDigest md;

	public DocumentDigester(Document document, String algorithm)
		throws NoSuchAlgorithmException
	{
		super(document);
		this.md = MessageDigest.getInstance(algorithm);
	}

	public DocumentDigester walk()
	{
		md.reset();
		super.walk();
		return this;
	}

	public byte[] getDigest()
	{
		return md.digest();
	}

	@Override
	protected State initialState()
	{
		return new DigesterState();
	}

	private final static byte[] NULL = "(null)".getBytes();
	private final static byte[] OPEN = "(".getBytes();
	private final static byte[] CLOSE = ")".getBytes();
	private final static byte[] COLON = ":".getBytes();

	private class DigesterState
		extends State
	{
		@Override
		public State visitScalar(Object value) {
			md.update(OPEN);
			md.update(value.toString().getBytes());
			md.update(CLOSE);
			return this;
		}

		@Override
		public State visitDate(Date date) {
			return visitScalar(Long.toString(date.getTime(), 16));
		}

		@Override
		public State visitNull() {
			md.update(NULL);
			return this;
		}

		@Override
		public State close() {
			md.update(CLOSE);
			return this;
		}

		@Override
		public State openList(int size) {
			md.update(OPEN);
			return this;
		}

		@Override
		public State openMap(int size) {
			return openList(size);
		}

		@Override
		public State openMapEntry(String key) {
			md.update(OPEN);
			md.update(key.getBytes());
			md.update(COLON);
			return this;
		}
	}
}
