package org.openxava.actions;

/**
 * @author Javier Paniza
 */

public class CancelAction extends ViewBaseAction implements INavigationAction {
	
	private boolean keyEditable = false;
	private boolean editable = true;
	private boolean restoreEditable = false;
	
	public void execute() throws Exception {		
		closeDialog(); 
		if (restoreEditable && getView() != null) {
			getView().setKeyEditable(keyEditable);
			getView().setEditable(editable);			
		}
	}
	
	public String [] getNextControllers() {
		return PREVIOUS_CONTROLLERS;		
	}
	
	public String getCustomView() {
		return PREVIOUS_VIEW; 
	}

	public boolean isRestoreEditable() {
		return restoreEditable;
	}
	public void setRestoreEditable(boolean restoreEditable) {
		this.restoreEditable = restoreEditable;
	}
		
}
