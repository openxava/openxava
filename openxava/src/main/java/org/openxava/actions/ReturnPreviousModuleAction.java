package org.openxava.actions;




/**
 * @author Javier Paniza
 */

public class ReturnPreviousModuleAction extends BaseAction implements IChangeModuleAction {

	private boolean reinit = false; 
	
	public void execute() throws Exception {
	}

	public String getNextModule() {
		return PREVIOUS_MODULE;		
	}

	public boolean hasReinitNextModule() { 
		return reinit; 
	}

	/** @since 7.4.2 */
	public boolean isReinit() {
		return reinit;
	}

	/** @since 7.4.2 */
	public void setReinit(boolean reinit) {
		this.reinit = reinit;
	}

}
