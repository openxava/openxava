package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;

/**
 * tmr
 * 
 * @author Javier Paniza
 */
public class SaveDeliveryFromDeliveryTypeAction extends SaveElementInCollectionAction {
	
	protected void saveCollectionElement(Map containerKey) throws Exception {
		super.saveCollectionElement(containerKey);
		Map key = getCollectionElementView().getKeyValues();
		addMessage("type=" + key.get("type"));
	}

}
