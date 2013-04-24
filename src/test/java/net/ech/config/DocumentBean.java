package net.ech.config;

import net.ech.doc.Document;

public class DocumentBean
{
	private Document property;
	public DocumentBean() {}
	public DocumentBean(Document property) { setProperty(property); }
	public Document getProperty() { return property; }
	public void setProperty(Document property) { this.property = property; }
}
