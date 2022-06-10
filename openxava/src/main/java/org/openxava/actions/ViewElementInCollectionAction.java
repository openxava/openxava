package org.openxava.actions;

/**
 * 
 * @author Javier Paniza
 */

public class ViewElementInCollectionAction extends EditElementInCollectionAction {
		
	public void execute() throws Exception {		
		getCollectionElementView().setKeyEditable(false);		
		super.execute();
		removeActions(
			getCollectionElementView().getSaveCollectionElementAction(), 	 
			getCollectionElementView().getRemoveCollectionElementAction());
	}
}
