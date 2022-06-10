package org.openxava.actions;




/**
 * @author Javier Paniza
 */

public class ChangeModuleAction extends BaseAction implements IChangeModuleAction {
	
	private String nextModule;
	private boolean reinit;
	
	
	public void execute() throws Exception {
	}

	public String getNextModule() {
		return nextModule;
	}
	public void setNextModule(String siguienteModulo) {
		this.nextModule = siguienteModulo;
	}

	public boolean hasReinitNextModule() {		
		return reinit;
	}
	public boolean isReinit() {
		return reinit;
	}
	public void setReinit(boolean reinit) {
		this.reinit = reinit;
	}
}
