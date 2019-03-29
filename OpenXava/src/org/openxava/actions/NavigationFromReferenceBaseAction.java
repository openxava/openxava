package org.openxava.actions;

import org.openxava.controller.meta.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

abstract public class NavigationFromReferenceBaseAction extends ReferenceBaseAction implements IChangeControllersAction, IChainAction {
		
	private String model;	
	private String controller;		
	private boolean showDialog = true; 
	
	abstract public String getNextAction() throws Exception;
	abstract protected String getCustomController();
	abstract protected String getDefaultController();
	
	
	
	public void execute() throws Exception {
		super.execute();
		if (showDialog) showDialog(); 
		else showNewView();		
		getView().setModelName(getModel());
		getView().putObject("xava.referenceSubview", getReferenceSubview());
		
		// Next line is for reset the cache		
		getRequest().getSession().removeAttribute(getKeyProperty() + ".descriptionsCalculator");				
	}

	public String[] getNextControllers() throws Exception {		
		return new String[] { getController() };
	}
	
	public String getModel() {
		return model;
	}

	public void setModel(String string) {
		model = string;
	}

	public String getController() throws XavaException {
		if (controller == null) {
			String controllerModel = getCustomController();			
			if (MetaControllers.contains(controllerModel)) {
				controller = controllerModel;
			}
			else {
				controller = getDefaultController();
			}
		}
		return controller;
	}

	public void setController(String string) {
		controller = string;
	}
	
	public boolean isShowDialog() {
		return showDialog;
	}
	public void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;
	}
	
	
}
