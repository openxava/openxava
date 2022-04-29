package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class GoProposeNameAction extends BaseAction implements ICustomViewAction {
	
	public void execute() throws Exception {
		setControllers("ProposeName");
	}
	
	public String getCustomView() {
		return "custom-jsp/proposeName";
	}

}
