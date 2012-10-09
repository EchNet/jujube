package net.ech.io.template;

import net.ech.codec.*;
import net.ech.io.*;
import net.ech.util.*;
import net.ech.util.JavascriptLexer;
import java.io.*;
import java.util.*;

/**
 * ContentHandle that expands a template on demand.
 */
public class TemplateContentHandle
	extends ProxyContentHandle
{
	public final static String STASH_START = "{{";
	public final static String STASH_END = "}}";

	private Evaluator evaluator;

	public TemplateContentHandle(ContentHandle source, Evaluator evaluator)
	{
		super(source);
		this.evaluator = evaluator;
	}

	@Override
	public CacheAdvice getCacheAdvice()
	{
		return CacheAdvice.DONT_CACHE;
	}

	@Override
	public String getVersion()
		throws IOException
	{
		throw new RuntimeException("TemplateContentHandle.getVersion not yet implemented");
	}

	@Override
	public Object getDocument()
		throws IOException
	{
		if (ContentTypes.isJson(getContentType())) {
			return expandJson(new DQuery(super.getDocument()).copyDoc().get());
		}
		else if (ContentTypes.isText(getContentType())) {
			StringWriter writer = new StringWriter();
			write(writer);
			return writer.toString();
		}
		else {
			return super.getDocument();
		}
	}

	@Override
	public void write(OutputStream outputStream)
		throws IOException
	{
		Writer writer = new OutputStreamWriter(outputStream, getCodec().getCharacterEncoding());
		write(writer);
        writer.flush();
	}

	@Override
	public void write(Writer writer)
		throws IOException
	{
		if (ContentTypes.isJavascript(getContentType())) {
			writeExpandedJavascript(super.getDocument().toString(), writer);
		}
		else if (ContentTypes.isJson(getContentType())) {
			writeExpandedJson(super.getDocument(), writer);
		}
		else if (ContentTypes.isText(getContentType())) {
			writeExpandedText(super.getDocument().toString(), writer);
		}
		else {
			super.write(writer);
		}
	}

	//
	// TODO: traversal of generic structure is the responsibility of class DQuery, not of this class! 
	// Rebase this code on DQuery.copyDoc().
	//
	private Object expandJson(Object root)
		throws IOException
	{
		if (root instanceof Map) {
			for (Map.Entry<String,Object> entry : ((Map<String,Object>)root).entrySet()) {
				entry.setValue(expandJson(entry.getValue()));
			}
		}
		else if (root instanceof List) {
			for (ListIterator<Object> iter = ((List<Object>) root).listIterator(); iter.hasNext(); ) {
				iter.set(expandJson(iter.next()));
			}
		}
		else if (root instanceof String) {
			ContentHandle subst = expandTokens((String) root);
			if (subst != null) {
				return subst.getDocument();
			}
		}
		return root;
	}

	private void writeExpandedJson(Object root, Writer writer)
		throws IOException
	{
		// Specialize the JsonCodec class!
		new JsonCodec()
		{
			// Trap string literals before they are written.
			@Override
			protected void writeStringLiteral(String value, Writer writer)
				throws IOException
			{
				// Does this string literal contain a substitution expression?
				ContentHandle subst = expandTokens(value);
				if (subst == null) {
					// If not, write out the original string literal.
					super.writeStringLiteral(value, writer);
				}
				else {
					// If so, check the content type of the substitution.
					String contentType = subst.getCodec().getContentType();
					if (ContentTypes.isJson(contentType)) {
						// Compatible content type - let the substitution content flow through without decoding.
						subst.write(writer);
					}
					else {
						// Decode the document and re-encode as JSON.
						new JsonCodec().encode(subst.getDocument(), writer);
					}
				}
			}
		}.encode(root, writer);
	}

	private void writeExpandedJavascript(String text, Writer writer)
		throws IOException
	{
		for (JavascriptLexer lexer = new JavascriptLexer(text); lexer.advance() >= 0; )
		{
			String token = lexer.getText();
			switch (lexer.getTokenType())
			{
			case JavascriptLexer.STRING_LITERAL:
				ContentHandle subst = expandTokens(token.substring(1, token.length() - 1));
				if (subst != null) {
					subst.write(writer);
					break;
				}
			default:
				writer.write(token);
			}
		}
	}

	private void writeExpandedText(String text, Writer writer)
		throws IOException
	{
		ContentHandle subst = expandTokens(text);
		if (subst != null) {
			writer.write(subst.getDocument().toString());
		}
		else {
			writer.write(text);
		}
	}

	private ContentHandle expandTokens(String text)
		throws IOException
	{
		ContentHandle result = null;
		if (text.startsWith(STASH_START)) {
			String expr = text.substring(STASH_START.length());
			if (expr.endsWith(STASH_END)) {
				expr = expr.substring(0, expr.length() - STASH_END.length());
			}
			result = evaluator.evaluateExpression(expr);
		}
		if (result != null && !ContentTypes.isText(result.getCodec().getContentType())) {
			throw new IOException(text + ": reference to bad substitution type " + result.getCodec().getContentType());
		}
		return result;
	}
}
