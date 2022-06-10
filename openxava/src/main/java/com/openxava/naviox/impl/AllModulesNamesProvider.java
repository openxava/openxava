/*
 * NaviOX. Navigation and security for OpenXava applications.
 * Copyright 2014 Javier Paniza Lucas
 *
 * License: Apache License, Version 2.0.	
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package com.openxava.naviox.impl;

import java.util.*;

import javax.persistence.*;

import org.openxava.application.meta.*;
import org.openxava.component.parse.*;
import org.openxava.util.*;

/**
 * @since 5.6 
 * @author Javier Paniza
 */
public class AllModulesNamesProvider implements IAllModulesNamesProvider {

	public Collection<String> getAllModulesNames(MetaApplication app) {
		Collection<String> allModulesNames = new HashSet<String>(app.getModulesNames());
		allModulesNames.remove("FirstSteps"); 
		for (String className: AnnotatedClassParser.getManagedClassNames()) {
			if (moduleForClass(className)) {
				allModulesNames.add(Strings.lastToken(className, "."));
			}
		}		
		return allModulesNames;
	}
	
	protected boolean moduleForClass(String className) {
		if (className.endsWith(".GalleryImage") || 
			className.endsWith(".AttachedFile") ||
			className.endsWith(".EmailSubscription") ||
			className.endsWith(".DiscussionComment")) 
		{
			return false;
		}
		if (isEmbeddable(className)) return false;
		return true;
	}
	
	private static boolean isEmbeddable(String className) { 
		try {
			return Class.forName(className).isAnnotationPresent(Embeddable.class);
		} 
		catch (ClassNotFoundException e) {
			return false;
		}		
	}

}
