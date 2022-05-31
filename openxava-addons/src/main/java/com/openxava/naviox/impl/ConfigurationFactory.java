package com.openxava.naviox.impl;

import com.openxava.naviox.model.*;

/**
 * tmr
 * 
 * @since 7.0
 * @author Javier Paniza
 */
public class ConfigurationFactory implements IConfigurationFactory {

	public Configuration create() {
		return new BasicConfiguration();
	}

}
