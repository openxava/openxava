package org.openxava.actions;

import java.util.*;
import org.openxava.view.*;

/**
 * Update (update, create, etc) a reference from another view, and returns to 
 * the previous view after it. <p>
 * 
 * @author Javier Paniza
 */

public abstract class UpdateReferenceBaseAction extends ViewBaseAction implements IChangeControllersAction {
	
	private String [] nextControllers = null;	
	
	protected void returnsToPreviousViewUpdatingReferenceView(Map key) throws Exception {
		nextControllers = PREVIOUS_CONTROLLERS;				
		if (!getPreviousViews().empty()) {				
			View referenceSubview = (View) getView().getObject("xava.referenceSubview");
			referenceSubview.setRequest(getView().getRequest()); 
			referenceSubview.setValuesNotifying(key);
			if (!referenceSubview.hasKeyProperties()) {
				referenceSubview.findObject();
			}
			closeDialog(); 
		}
		resetDescriptionsCache();
	}
	
	protected Map getValuesToSave() throws Exception {
		return getView().getValues();
	}
	
	public String[] getNextControllers() {		
		return nextControllers;
	}
	
}
