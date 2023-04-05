/*
 * XavaPhone. Mobile UI for OpenXava application (unimplemented).
 * Copyright 2014 Javier Paniza Lucas
 *
 * License: Apache License, Version 2.0.	
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package com.openxava.phone.impl;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.util.*;

import com.openxava.naviox.impl.*;

/**
 * 
 * @since 7.0
 * @author Javier Paniza
 */
public class PhoneServletProvider implements IServletProvider {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* tmr
		response.getWriter().print("<html><head></head><body style='font-size: 500%;'>"); 
		response.getWriter().print(XavaResources.getString(request.getLocale(), "mobile_ui_xavapro", "<a href='http://www.openxava.org/xavapro'>XavaPro</a>"));  
		response.getWriter().print("</body>");
		*/
		// tmr ini
		response.getWriter().print("<html><head></head><body><font size=7>"); 
		response.getWriter().print(XavaResources.getString(request.getLocale(), "mobile_ui_xavapro", "<a href='http://www.openxava.org/xavapro'>XavaPro</a>"));  
		response.getWriter().print("</font></body>");		
		response.setContentType("text/html"); // tmr En migration
		// tmr fin

	}

}
