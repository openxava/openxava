package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class NewDeliveryDetailAction extends CreateNewElementInCollectionAction {
	
	public void execute() throws Exception {
		super.execute();
		Object numberView = getView().getValue("number");
		Object numberParentView = getParentView().getValue("number");
		getCollectionElementView().setValue("description", "DETAIL FOR DELIVERY " + numberView + "/" + numberParentView);
		getCollectionElementView().setFocus("description"); 
		addMessage("delivery_detail_action_executed", "new");
	}

}
