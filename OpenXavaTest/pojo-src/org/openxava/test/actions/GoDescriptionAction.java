package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class GoDescriptionAction extends BaseAction implements IForwardAction {
	
	public void execute() throws Exception {
	}

	public String getForwardURI() {
		return "/doc/description_en.html";
	}
	
	public boolean inNewWindow() {
		return false;
	}
	
}
