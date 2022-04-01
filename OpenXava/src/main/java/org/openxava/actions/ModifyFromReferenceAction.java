package org.openxava.actions;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.view.*;



/**
 * @author Javier Paniza
 */

public class ModifyFromReferenceAction extends NavigationFromReferenceBaseAction  {
	private static Log log = LogFactory.getLog(ModifyFromReferenceAction.class);
	
	private boolean exists = true;
	
	public void execute() throws Exception {		
		super.execute();
		
		Map key = getReferenceSubview().getKeyValuesWithValue();
		if (key.isEmpty()) {
			closeDialog();
			exists = false;
			addError("cannot_modify_empty_reference");
			return;
		}	
			
		getView().setKeyEditable(false);
		getView().setValues(key);		
	}
		
	public String getCustomController() {	
		return getModel() + "Modification";
	}
	
	public String getDefaultController() {
		return "Modification";
	}
	
	public String[] getNextControllers() throws Exception {		
		return exists?super.getNextControllers():SAME_CONTROLLERS;
	}
	
	public String getNextAction() throws Exception {
		return exists?getController() + ".search":null;
	}

}
