package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class GoMyPublicJSPInSameWindowCalculatedAction extends BaseAction implements IForwardAction {
	
	private boolean inNewWindow = true;

	public void execute() throws Exception {
		inNewWindow = false;
	}

	public String getForwardURI() {
		return "/public/myPublicJSP.jsp";
	}

	public boolean inNewWindow() {
		return inNewWindow;
	}

}
