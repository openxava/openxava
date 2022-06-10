package com.openxava.naviox.impl;

import javax.servlet.http.*;

/**
 * 
 * @since 7.0
 * @author Javier Paniza
 */
public interface IFoldersProvider {
	
	String goFolder(HttpServletRequest request, HttpServletResponse response, String folderOid);
	
	String goBack(HttpServletRequest request, HttpServletResponse response);

}
