package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class ChangeName extends ViewBaseAction {
	
	public void execute() throws Exception {
		String name = getView().getValueString("name");
		closeDialog();
		getView().setValue("name", name);
	}

}
