package com.openxava.naviox.impl;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.application.meta.*;

import com.openxava.naviox.*;

/**
 * tmr
 * @version 7.0
 * @author Javier Paniza
 */
public interface IModulesHelperProvider {
	
	void init(String applicationName);
	
	String getCurrent(HttpServletRequest request); 
	
	String getUserAccessModule(ServletRequest request);
	
	List<MetaModule> getAll(HttpServletRequest request);
	
	List<MetaModule> getNotInMenu();
	
	Collection<MetaModule> getInMenu(HttpServletRequest request, Modules modules);

	boolean isPublic(HttpServletRequest request, String moduleName);
	
	boolean showsIndexLink();

	boolean showsSearchModules(HttpServletRequest request);

}
