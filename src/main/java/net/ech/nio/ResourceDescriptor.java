package net.ech.nio;

import net.ech.config.*;
import net.ech.util.*;

/**
 * Configuration helper metadata.
 */
public abstract class ResourceDescriptor
{
    public static SubtypeDescriptor[] getSubtypeDescriptors()
	{
		return new SubtypeDescriptor[] {
			new SubtypeDescriptor(FileResource.class, new DPredicate() {
				public boolean evaluate(DQuery config) {
					try {
						DQuery base = config.find("base");
						return !base.isNull() && !base.require(String.class).matches("^[a-z]+:.+$");
					}
					catch (DocumentException e) {
						return false;
					}
				}
			}),
			new SubtypeDescriptor(UrlResource.class, new DPredicate() {
				public boolean evaluate(DQuery config) {
					try {
						DQuery base = config.find("base");
						return !base.isNull() && base.require(String.class).matches("^[a-z]+:.+$");
					}
					catch (DocumentException e) {
						return false;
					}
				}
			})
		};
	}
}
