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

package com.openxava.phone.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
@WebServlet(name = "phone", urlPatterns = "/p/*")
public class PhoneServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(PhoneServlet.class);
	private static IServletProvider provider;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getProvider().doGet(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private static IServletProvider getProvider() {
		if (provider == null) {
			try {
				provider = (IServletProvider) Class.forName(NaviOXPreferences.getInstance().getPhoneServletProviderClass()).newInstance();
			} 
			catch (Exception ex) {
				log.warn(XavaResources.getString("provider_creation_error", "OrganizationServlet"), ex);
				throw new XavaException("provider_creation_error", "OrganizationServlet");
			}
		}
		return provider;
	}

}
