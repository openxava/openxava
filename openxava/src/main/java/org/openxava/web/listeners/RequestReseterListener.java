package org.openxava.web.listeners;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 *  
 * @since 6.5.1
 * @author Javier Paniza
 */

@WebListener
public class RequestReseterListener implements ServletRequestListener {
	
	public void requestDestroyed(ServletRequestEvent sre) {
		try {
			Users.setCurrent((String) null);
			XPersistence.reset();
		// tmr ini	
		}
		catch (Exception ex) {
			System.out.println("[RequestReseterListener.requestDestroyed] Falla al cerrar XPersistence"); // tmr
			ex.printStackTrace();
			
		}
		// tmr fin
	}
	
	public void requestInitialized(ServletRequestEvent sre) {
		HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
		Users.setCurrent(request);
		Locales.setCurrent(request); 
		SessionData.setCurrent(request); 
	}

}