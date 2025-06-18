package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */

public class UpdateWarehouseFromReferenceAction extends UpdateAction {
	
	@Override
	public void execute() throws Exception {
		String name = getView().getValueString("name"); // Get name before closing the dialog
		super.execute(); // Updates the warehouse and closes the dialog (if not validation errors)
		if (!getErrors().contains() && name.equals("MODIFIED WAREHOUSE")) {
			addWarning("warehouse_modified_using_default_name"); // Message in i18n messages file  
		}
	}

}
