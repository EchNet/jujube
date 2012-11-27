package net.ech.service;

import net.ech.config.*;
import net.ech.util.*;

/**
 * Configuration helper metadata.
 */
public class ServiceModuleDescriptor
	extends TypeDescriptor
{
	@Override
	public Class<?> getType()
	{
		return ServiceModule.class;
	}

	@Override
	public SubtypeDescriptor[] getSubtypeDescriptors()
	{
		return new SubtypeDescriptor[] {
			new SubtypeDescriptor(GetResourceServiceModule.class, new DPredicate() {
				public boolean evaluate(DQuery config) {
					return true;
				}
			})
		};
	}
}
