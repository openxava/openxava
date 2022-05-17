package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class GoMyPublicJSPInNewWindowCalculatedAction extends BaseAction implements IForwardAction {
	
	private boolean inNewWindow = false;

	public void execute() throws Exception {
		inNewWindow = true;
	}

	public String getForwardURI() {
		return "/public/myPublicJSP.jsp";
	}

	public boolean inNewWindow() {
		return inNewWindow;
	}

}
