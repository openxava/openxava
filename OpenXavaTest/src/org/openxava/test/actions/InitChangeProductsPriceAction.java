package org.openxava.test.actions;

import org.openxava.actions.*;


/**
 * @author Javier Paniza
 */
public class InitChangeProductsPriceAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().setEditable("description", false);
	}

}
