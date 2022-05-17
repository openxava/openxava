package com.openxava.naviox.impl;

import java.util.*;

import org.openxava.application.meta.*;

/**
 * @since 5.6
 * @author Javier Paniza
 */
public interface IAllModulesNamesProvider {
	
	Collection<String> getAllModulesNames(MetaApplication app);

}
