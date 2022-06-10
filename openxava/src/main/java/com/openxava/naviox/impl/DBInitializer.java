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

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.*;

/**
 * 
 * @since 7.0
 * @author Javier Paniza
 */

public class DBInitializer {
	
	private static Log log = LogFactory.getLog(Modules.class);

	public static void init() {
		try {
			Class dbClass = Class.forName("com.openxava.naviox.impl.DB");
			XObjects.execute(dbClass, "init");
		} 
		catch (ClassNotFoundException e) {
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Exception ex) {
			// Never
			log.error(ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}		
	}

}
