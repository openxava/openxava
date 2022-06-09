package com.openxava.naviox.impl;

import javax.servlet.*;

/**
 * @since 7.0
 * @author Javier Paniza
 */
public interface IInitializerProvider {
	
	void init(ServletRequest request);

}
