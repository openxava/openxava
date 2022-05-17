package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 *  
 * @author Javier Paniza 
 */

public class AddToNameAction extends ViewBaseAction {

	private String stringToAdd;	
	
	public void execute() throws Exception {
		Object name = getView().getValue("name");
		getView().setValue("name", name + getStringToAdd());
	}

	public String getStringToAdd() {
		return stringToAdd;
	}

	public void setStringToAdd(String stringToAdd) {
		this.stringToAdd = stringToAdd;
	}
	
}
