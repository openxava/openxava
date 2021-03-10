package org.openxava.web.listeners;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 *  
 * @since 6.5.1
 * @author Javier Paniza
 */

@WebListener
public class RequestCleanerListener implements ServletRequestListener {
	
	public void requestDestroyed(ServletRequestEvent sre) {
		Users.setCurrent((String) null);
		XPersistence.reset();
		XHibernate.reset();
	}
	
	public void requestInitialized(ServletRequestEvent sre) {
		Users.setCurrent((HttpServletRequest)sre.getServletRequest());
	}

}