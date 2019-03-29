package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class GoMyPublicJSPInNewWindowAction extends BaseAction implements IForwardAction {

	public void execute() throws Exception {
	}

	public String getForwardURI() {
		return "/public/myPublicJSP.jsp";
	}

	public boolean inNewWindow() {
		return true;
	}

}
