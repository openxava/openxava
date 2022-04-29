package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class ProposeNameAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		String name = getView().getValueString("name");
		addMessage("name_confirmation", name);	
	}

}
