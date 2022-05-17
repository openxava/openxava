package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class CreateNewWarehouseFromReferenceAction extends NewAction {
		
	public void execute() throws Exception {		
		super.execute();
		getView().setValue("name", "NEW WAREHOUSE");
	}

}
