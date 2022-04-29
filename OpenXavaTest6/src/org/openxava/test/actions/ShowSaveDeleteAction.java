package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class ShowSaveDeleteAction extends BaseAction implements IShowActionsAction {

	public String[] getActionsToShow() {
		return new String [] { "CRUD.save", "CRUD.delete" };
	}

	public void execute() throws Exception {
	}

}
