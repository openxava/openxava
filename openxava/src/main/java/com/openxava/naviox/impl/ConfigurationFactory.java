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

import com.openxava.naviox.model.*;

/**
 * 
 * @since 7.0
 * @author Javier Paniza
 */
public class ConfigurationFactory implements IConfigurationFactory {

	public Configuration create() {
		return new BasicConfiguration();
	}

}
