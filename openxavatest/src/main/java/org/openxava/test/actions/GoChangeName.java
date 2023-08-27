package org.openxava.test.actions;

import org.openxava.actions.*;

public class GoChangeName extends ViewBaseAction {
	
	public void execute() throws Exception {
		showDialog();
		getView().setModelName("Name");	
		getView().setLabelId("name", "Change name to");
		setControllers("ChangeName");		
	}

}
