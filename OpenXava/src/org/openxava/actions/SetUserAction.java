package org.openxava.actions;

import javax.servlet.http.HttpServletRequest;

import org.openxava.actions.BaseAction;
import org.openxava.util.Is;

/**
 * 
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
