package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class HideDeleteAction extends BaseAction implements IHideActionAction {

	public String getActionToHide() {
		return "CRUD.delete";
	}

	public void execute() throws Exception {
	}

}
