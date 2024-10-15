package org.openxava.actions;




/**
 * @author Javier Paniza
 */

public class ReturnPreviousModuleAction extends BaseAction implements IChangeModuleAction {

	private boolean reinit = false; // tmr En changelog
	
	public void execute() throws Exception {
	}

	public String getNextModule() {
		return PREVIOUS_MODULE;		
	}

	public boolean hasReinitNextModule() { 
		// tmr return false;
		return reinit; // tmr
	}

	public boolean isReinit() {
		return reinit;
	}

	public void setReinit(boolean reinit) {
		this.reinit = reinit;
	}

}
