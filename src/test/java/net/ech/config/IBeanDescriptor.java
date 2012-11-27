package net.ech.config;

import net.ech.util.*;

public abstract class IBeanDescriptor
{
	public static SubtypeDescriptor[] getSubtypeDescriptors()
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
