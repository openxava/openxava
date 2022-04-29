package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class SaveElementInCollectionFailingAction extends SaveElementInCollectionAction {
	
	public void execute() throws Exception {
		if (true) {
			addError("add_to_collection_error");
			return;
		}
		super.execute();		
	}	

}
