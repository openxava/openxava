package org.openxava.web.listeners;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 *  
 * @since 6.5.1
 * @author Javier Paniza
 */

@WebListener
public class RequestReseterListener implements ServletRequestListener {
	
	private static Log log = LogFactory.getLog(RequestReseterListener.class); 
	
	public void requestDestroyed(ServletRequestEvent sre) {
		try {
			Users.setCurrent((String) null);
			XPersistence.reset();
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("destroying_request_problems"), ex);			
		}
	}
	
	public void requestInitialized(ServletRequestEvent sre) {
		HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
		Users.setCurrent(request);
		Locales.setCurrent(request); 
		SessionData.setCurrent(request); 
	}

}