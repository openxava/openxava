package com.openxava.naviox.impl;

import javax.servlet.http.*;

/**
 * 
 * @since 7.0
 * @author Javier Paniza
 */
public interface IPhoneListProvider {
	
	String filter(HttpServletRequest request, HttpServletResponse response, String application, String module, String searchWord);

}
