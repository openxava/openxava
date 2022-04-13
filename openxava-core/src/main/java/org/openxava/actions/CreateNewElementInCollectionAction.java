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
		if (!isParentSaved()) validateViewValues(); 
		getCollectionElementView().reset();						
		getCollectionElementView().setCollectionDetailVisible(true); 
		getCollectionElementView().setCollectionEditingRow(-1);
		showDialog(getCollectionElementView());				
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
		Iterator itDetailActions = getCollectionElementView().getActionsNamesDetail().iterator();		
		while (itDetailActions.hasNext()) {			
			addActions(itDetailActions.next().toString());			
		}
		addActions(getCollectionElementView().getHideCollectionElementAction());
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
