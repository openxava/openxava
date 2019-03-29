package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class RemoveSelectedDeliveryDetailsAction extends RemoveSelectedInCollectionAction {
	
	protected void removeElement(Map values) throws Exception {
		super.removeElement(values);
		Object number = values.get("number");
		addMessage("delivery_detail_removed", number);		
	}

}
