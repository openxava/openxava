package org.openxava.actions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openxava.model.MapFacade;
import org.openxava.util.XavaException;


/**
 * @author Javier Paniza
 * Modified by Federico Alcantara. Fix bug 2976466.
 */

public class EditElementInCollectionAction extends CollectionElementViewBaseAction  {
	
	private int row;
	
	public void execute() throws Exception {
		getCollectionElementView().clear(); 
		getCollectionElementView().setKeyEditable(false); 
		getCollectionElementView().setCollectionDetailVisible(true);
		Collection elements;
		Map keys = null;
		Map	values = null;
		if (getCollectionElementView().isCollectionFromModel()) {		
			elements = getCollectionElementView().getCollectionValues();
			if (elements == null) return;
			if (elements instanceof List) {
				keys = (Map) ((List) elements).get(getRow());			
			}
		} else {
			keys = (Map) getCollectionElementView().getCollectionTab().getTableModel().getObjectAt(row);
		}
				
		if (keys != null) {
			values = MapFacade.getValues(getCollectionElementView().getModelName(), keys, getCollectionElementView().getMembersNames());
			getCollectionElementView().setValues(values);					
			getCollectionElementView().setCollectionEditingRow(getRow());
		} else {
			throw new XavaException("only_list_collection_for_aggregates");
		}
		showDialog(getCollectionElementView());		
		if (getCollectionElementView().isCollectionEditable() || 
			getCollectionElementView().isCollectionMembersEditables()) 
		{ 
			addActions(getCollectionElementView().getSaveCollectionElementAction());
		}
		if (getCollectionElementView().isCollectionEditable()) { 
			addActions(getCollectionElementView().getRemoveCollectionElementAction());
		} 	
		Iterator itDetailActions = getCollectionElementView().getActionsNamesDetail().iterator();
		while (itDetailActions.hasNext()) {		
			addActions(itDetailActions.next().toString());			
		}
		addActions(getCollectionElementView().getHideCollectionElementAction());
	}
		
	public int getRow() {
		return row;
	}

	public void setRow(int i) {
		row = i;
	}

}
