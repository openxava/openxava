package org.openxava.test.actions;

import org.openxava.actions.*;

public class GoAddFullAddressAction extends ViewBaseAction {

	public void execute() throws Exception {
		showDialog(); 
		getView().setTitleId("entry_full_address");
		// getView().setTitle("Entry the full address");
		getView().setModelName("OneLineAddress");		
		// setControllers("AddFullAddress", "Dialog");
		addActions("AddFullAddress.add", "Dialog.cancel");
	}

}
