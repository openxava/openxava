package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class ShowDeleteAction extends BaseAction implements IShowActionAction {

	public String getActionToShow() {
		return "CRUD.delete";
	}

	public void execute() throws Exception {
	}

}
