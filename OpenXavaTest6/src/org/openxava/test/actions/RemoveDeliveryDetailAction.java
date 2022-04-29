package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class RemoveDeliveryDetailAction extends RemoveElementFromCollectionAction {

	public void execute() throws Exception {		
		super.execute();
		addMessage("delivery_detail_action_executed", "remove");
	}

}
