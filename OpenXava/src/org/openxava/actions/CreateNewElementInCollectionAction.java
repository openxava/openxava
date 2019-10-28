package org.openxava.actions;

import java.util.*;

import org.openxava.model.*;
import org.openxava.util.XavaPreferences;
import org.openxava.view.*;


/**
 * Create a new element in collection. <p>
 * 
 * Since 4m5 creates two buttons for the save action, 
 * ones that closes the dialog and the other that stays.
 * 
 * @author Javier Paniza
 * 
 */

public class CreateNewElementInCollectionAction extends CollectionElementViewBaseAction {
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		System.out.println("[CreateNewElementInCollectionAction.execute] 10"); // tmp
		if (!isParentSaved()) validateViewValues(); 
		System.out.println("[CreateNewElementInCollectionAction.execute] 20"); // tmp
		getCollectionElementView().reset();						
		System.out.println("[CreateNewElementInCollectionAction.execute] 30"); // tmp
		getCollectionElementView().setCollectionDetailVisible(true); 
		getCollectionElementView().setCollectionEditingRow(-1);
		System.out.println("[CreateNewElementInCollectionAction.execute] 40"); // tmp
		showDialog(getCollectionElementView());				
		System.out.println("[CreateNewElementInCollectionAction.execute] 50"); // tmp
		if (getCollectionElementView().isCollectionEditable() || 
			getCollectionElementView().isCollectionMembersEditables()) 
		{ 
			// The Collection.saveAndStay will function as trapper for the save action,
			// and will prevent the dialog to close while clearing the form and filling with default values.
			String saveAction = getCollectionElementView().getSaveCollectionElementAction(); 
			addActions(saveAction);
			
			if(XavaPreferences.getInstance().isSaveAndStayForCollections() && !saveAction.equals("")){
				addActions("Collection.saveAndStay");
			}
			
			getCollectionElementView().setKeyEditable(true); 
		} 		
		System.out.println("[CreateNewElementInCollectionAction.execute] 60"); // tmp
		Iterator itDetailActions = getCollectionElementView().getActionsNamesDetail().iterator();		
		while (itDetailActions.hasNext()) {			
			addActions(itDetailActions.next().toString());			
		}
		addActions(getCollectionElementView().getHideCollectionElementAction());
		System.out.println("[CreateNewElementInCollectionAction.execute] 999"); // tmp
	}
	
	protected boolean isParentSaved() { 
		View view = getCollectionElementView().getParent();
		if (getView() == view) {
			if (view.isKeyEditable()) {				
				return false;								
			}			
		}			
		else {
			if (view.getKeyValuesWithValue().isEmpty()) {				
				return false;										
			}
		}
		return true;
	}
	
}
