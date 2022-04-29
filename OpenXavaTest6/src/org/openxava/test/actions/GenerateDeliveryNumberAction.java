package org.openxava.test.actions;

import org.openxava.actions.*;


/**
 * @author Javier Paniza
 */
public class GenerateDeliveryNumberAction extends ViewBaseAction {

	public void execute() throws Exception {		
		getView().setValue("number", new Integer(77)); 
	}

}
