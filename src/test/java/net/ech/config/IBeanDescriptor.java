package net.ech.config;

import net.ech.util.*;

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
			new SubtypeDescriptor(Bean.class, new DPredicate() {
				public boolean evaluate(DQuery config) {
					return !config.find("property").isNull();
				}
			}),
			new SubtypeDescriptor(Bean.class, new DPredicate() {
				public boolean evaluate(DQuery config) {
					return !config.find("properly").isNull();
				}
			}),
		};
	}
}
