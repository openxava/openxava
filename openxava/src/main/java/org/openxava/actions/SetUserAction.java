package org.openxava.actions;

import javax.servlet.http.*;

import org.openxava.util.*;

/**
 * tmr Quitar: Problema de seguridad
 * @author Federico Alcantar 
 */
public class SetUserAction extends BaseAction{

	public void execute() throws Exception {
		HttpServletRequest request = getRequest();
		String currentUser = request.getParameter("user");
		if (!Is.empty(currentUser)) {
			request.getSession().setAttribute("xava.user", currentUser);
		} else {
			request.getSession().setAttribute("xava.user", request.getRemoteUser());
		}
	}

}
