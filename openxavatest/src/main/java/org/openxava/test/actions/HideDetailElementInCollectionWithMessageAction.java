package org.openxava.test.actions;

import org.openxava.actions.CollectionElementViewBaseAction;

/**
 * @author Javier Paniza
 */

public class HideDetailElementInCollectionWithMessageAction extends CollectionElementViewBaseAction { 
	
	public void execute() throws Exception {
		getCollectionElementView().setCollectionDetailVisible(false);
		getCollectionElementView().setCollectionEditingRow(-1);
		addMessage("hideDetail_message");
		closeDialog();
	}
	
}
