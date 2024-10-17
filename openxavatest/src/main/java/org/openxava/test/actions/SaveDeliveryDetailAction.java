package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class SaveDeliveryDetailAction extends SaveElementInCollectionAction {
	
	public void execute() throws Exception {
		super.execute();
		addMessage("delivery_detail_action_executed", "save");
	}
	
	protected void saveCollectionElement(Map containerKey) throws Exception { // tmr
		super.saveCollectionElement(containerKey);
		System.out.println("[SaveDeliveryDetailAction.saveCollectionElement] containerKey=" + containerKey); // tmp
		System.out.println("[SaveDeliveryDetailAction.saveCollectionElement] etCollectionElementView().getKeyValues()=" + getCollectionElementView().getKeyValues()); // tmp
	}

}
