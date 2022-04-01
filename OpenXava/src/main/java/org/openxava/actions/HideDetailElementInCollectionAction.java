package org.openxava.actions;




/**
 * @author Javier Paniza
 */

public class HideDetailElementInCollectionAction extends CollectionElementViewBaseAction {
	
	
	
	public void execute() throws Exception {
		getCollectionElementView().setCollectionDetailVisible(false);
		getCollectionElementView().setCollectionEditingRow(-1);
		closeDialog(); 
	}

}
