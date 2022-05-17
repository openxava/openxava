package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class OnChangeVisitProductAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		getView().setValue("description", "PRODUCT: " + getNewValue());
	}

}
