package org.openxava.actions;

import java.util.*;

import org.openxava.model.*;
import org.openxava.util.*;


/**
 * @author Javier Paniza
 * Modified by Federico Alcantara. Fix bug 2976466.
 */

public class EditElementInCollectionAction extends CollectionElementViewBaseAction  {
	
	private int row;
	
	public void execute() throws Exception {
		System.out.println("EditElementInCollectionAction");
		getCollectionElementView().clear(); 
		getCollectionElementView().setKeyEditable(false); 
		getCollectionElementView().setCollectionDetailVisible(true);
		Collection elements;
		Map keys = null;
		Map	values = null;
		System.out.println("0"); 	
		if (getCollectionElementView().isCollectionFromModel()) {		
			elements = getCollectionElementView().getCollectionValues();
			if (elements == null) return;
			if (elements instanceof List) {
				keys = (Map) ((List) elements).get(getRow());			
			}
		} else {
			keys = (Map) getCollectionElementView().getCollectionTab().getTableModel().getObjectAt(row);
		}
		System.out.println("1");	
		if (keys != null) {
			values = MapFacade.getValues(getCollectionElementView().getModelName(), keys, getCollectionElementView().getMembersNames());
			getCollectionElementView().setValues(values);					
			getCollectionElementView().setCollectionEditingRow(getRow());
		} else {
			throw new XavaException("only_list_collection_for_aggregates");
		}
		showDialog(getCollectionElementView());	
		System.out.println("2");
		if (getCollectionElementView().isCollectionEditable() || 
			getCollectionElementView().isCollectionMembersEditables()) 
		{ 
			System.out.println("2.1");
			addActions(getCollectionElementView().getSaveCollectionElementAction());
		}
		if (getCollectionElementView().isCollectionEditable()) { 
			System.out.println("2.2");
			addActions(getCollectionElementView().getRemoveCollectionElementAction());
		} 	
		System.out.println("3");
		Iterator itDetailActions = getCollectionElementView().getActionsNamesDetail().iterator();
		while (itDetailActions.hasNext()) {		
			addActions(itDetailActions.next().toString());			
		}
		System.out.println("4");
		addActions(getCollectionElementView().getHideCollectionElementAction());
	}
		
	public int getRow() {
		return row;
	}

	public void setRow(int i) {
		row = i;
	}

}
