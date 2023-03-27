package org.openxava.actions;

import javax.inject.*;

/**
 * @author Javier Paniza
 */

public class NewAction extends ViewBaseAction implements IChangeModeAction, IModelAction {
	
	@Inject  
	private String date; 
	
	private String modelName; 
	private boolean restoreModel = false; 
	
	public void execute() throws Exception {
		if (restoreModel) getView().setModelName(modelName); 
		getView().setKeyEditable(true);
		getView().setEditable(true);
		getView().reset();
		if (getView().hasSections()) getView().setActiveSection(0);
	}
		
	public String getNextMode() {
		return IChangeModeAction.DETAIL;
	}

	public void setModel(String modelName) { 
		this.modelName = modelName;		
	}

	public boolean isRestoreModel() {
		return restoreModel;
	}

	public void setRestoreModel(boolean restoreModel) {
		this.restoreModel = restoreModel;
	}

	public String getDate() {
		return IChangeModeAction.DETAIL;
	}

	public void setDate(String date) { 
		System.out.println("set" + date);
		this.date = date;		
	}
}
