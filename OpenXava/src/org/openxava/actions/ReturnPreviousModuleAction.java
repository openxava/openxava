package org.openxava.actions;




/**
 * @author Javier Paniza
 */

public class ReturnPreviousModuleAction extends BaseAction implements IChangeModuleAction {

	
	
	public void execute() throws Exception {
	}

	public String getNextModule() {
		return PREVIOUS_MODULE;		
	}

	public boolean hasReinitNextModule() { 
		return false;
	}

}
