package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class ModifyWarehouseFromReferenceAction extends SearchByViewKeyAction {
		
	public void execute() throws Exception {		
		super.execute();		
		getView().setValue("name", "MODIFIED WAREHOUSE");
	}

}
