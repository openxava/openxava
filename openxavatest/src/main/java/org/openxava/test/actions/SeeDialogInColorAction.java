package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * Create on 24/04/2013 (11:31:16)
 * @author Ana Andres
 */
public class SeeDialogInColorAction extends ViewBaseAction{ 
	private int actionNumber;
	
	public void execute() throws Exception {
		showDialog();
		getView().setModelName("Color");
		getView().setViewName("Sub");
		addActions("Dialog.cancel");
		getView().setValue("actionNumber", getActionNumber());
	}

	public int getActionNumber() {
		return actionNumber;
	}

	public void setActionNumber(int actionNumber) {
		this.actionNumber = actionNumber;
	}
}
