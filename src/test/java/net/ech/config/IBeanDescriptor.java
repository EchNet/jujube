package net.ech.config;

import net.ech.doc.Document;
import net.ech.doc.DocPredicate;

public class IBeanDescriptor
	extends TypeDescriptor
{
	public Class<?> getType()
	{
		return IBean.class;
	}

	public SubtypeDescriptor[] getSubtypeDescriptors()
	{
		return new SubtypeDescriptor[] {
			new SubtypeDescriptor(Bean.class, new DocPredicate() {
				public boolean evaluate(Document config) {
					return !config.find("property").isNull();
				}
			}),
			new SubtypeDescriptor(Bean.class, new DocPredicate() {
				public boolean evaluate(Document config) {
					return !config.find("properly").isNull();
				}
			}),
		};
	}
}
