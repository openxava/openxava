package org.openxava.util;

import java.util.*;

import javax.servlet.http.*;

/**
 * By default it get parameters from i18n.
 * 
 * Create on 11/03/2011 (12:42:24)
 * @author Ana Andres
 */
public class DefaultReportParametersProvider implements IReportParametersProvider, IRequestReportParametersProvider{
	private transient HttpServletRequest request;
	
	public String getOrganization() {
		Locale locale = XavaResources.getLocale(request);
		if (Labels.exists("xava.organization", locale)) {
			return Labels.get("xava.organization", locale);
		}		
		else {
			return "";
		}
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
