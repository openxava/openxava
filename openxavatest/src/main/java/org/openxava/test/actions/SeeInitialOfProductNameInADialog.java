package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * Create on 03/06/2013 (09:12:12)
 * @author Ana Andres
 */
public class SeeInitialOfProductNameInADialog extends ViewBaseAction{

	public void execute() throws Exception {
		String d = getView().getValueString("description");
		
		showDialog();
		getView().setModelName("Product5");
		getView().setViewName("Dialog");
		getView().setValue("description", "The description start with: " + d.substring(0, 1));
		setControllers("Dialog");
	}

}
