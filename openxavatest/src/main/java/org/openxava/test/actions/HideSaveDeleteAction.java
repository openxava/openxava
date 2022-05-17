package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class HideSaveDeleteAction extends BaseAction implements IHideActionsAction {

	public String[] getActionsToHide() {
		return new String [] { "CRUD.save", "CRUD.delete" };
	}

	public void execute() throws Exception {
	}

}
