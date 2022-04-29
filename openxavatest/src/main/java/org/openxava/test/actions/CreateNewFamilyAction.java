package org.openxava.test.actions;

import org.openxava.actions.*;


/**
 * @author Javier Paniza
 */
public class CreateNewFamilyAction extends ViewBaseAction implements IChangeModeAction {

	public void execute() throws Exception {
		getView().setValue("number", new Integer(99));
		getView().setValue("description", "NOVA FAMILIA");
	}

	public String getNextMode() {		
		return IChangeModeAction.DETAIL;
	}

}
