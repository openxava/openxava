package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class GoNoWhereInNewWindowAction extends BaseAction implements IForwardAction {

	public void execute() throws Exception {
	}

	public String getForwardURI() {
		return null;
	}

	public boolean inNewWindow() {
		return true; 
	}

}
