package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class ShowButtonsAction extends BaseAction {

	public void execute() throws Exception {
		getManager().showButtons();
	}

}
