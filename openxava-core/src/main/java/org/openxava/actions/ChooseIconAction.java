package org.openxava.actions;

import javax.inject.*;

/** 
 * 
 * @author Javier Paniza
 */

public class ChooseIconAction extends ViewBaseAction implements INavigationAction {
	
	@Inject
	private String newIconProperty;

	private String icon;

	public void execute() throws Exception {
		closeDialog();
		getView().setValue(newIconProperty, icon);
	}
	
	public String[] getNextControllers() {		
		return PREVIOUS_CONTROLLERS;
	}

	public String getCustomView() {		
		return PREVIOUS_VIEW; 
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
