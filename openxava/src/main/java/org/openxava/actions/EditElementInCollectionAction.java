package org.openxava.actions;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.view.meta.*;


/**
 * @author Javier Paniza
 * Modified by Federico Alcantara. Fix bug 2976466.
 */

public class EditElementInCollectionAction extends CollectionElementViewBaseAction  {
	
	private static Log log = LogFactory.getLog(EditElementInCollectionAction.class);
	
	private int row;
	private int nextValue;
	private boolean openDialog = true;
	
	public void execute() throws Exception {
		getCollectionElementView().setCollectionDetailVisible(true); 
		getCollectionElementView().clear(); 
		getCollectionElementView().setKeyEditable(false); 
		Collection elements;
		Map keys = null;
		Map	values = null;
		if (getCollectionElementView().isCollectionFromModel()) {
			elements = getCollectionElementView().getCollectionValues();
			if (elements == null) return;
			int rowValue = getCollectionElementView().getCollectionEditingRow();
			row = (rowValue > 0) ? rowValue : getRow();
			if (elements instanceof List) {
				if (nextValue != 0) validRowAndUpdate(row, elements.size());
				keys = (Map) ((List) elements).get(row);		
			}
		} else {
			if (nextValue != 0) validRowAndUpdate(getCollectionElementView().getCollectionEditingRow(), getCollectionElementView().getCollectionSize());
			keys = (Map) getCollectionElementView().getCollectionTab().getTableModel().getObjectAt(row);
		}
				
		if (keys != null) {
			values = MapFacade.getValues(getCollectionElementView().getModelName(), keys, getCollectionElementView().getMembersNames());
			getCollectionElementView().setValues(values);					
			getCollectionElementView().setCollectionEditingRow(getRow());
		} else {
			throw new XavaException("only_list_collection_for_aggregates");
		}
		setViewNameForEditIfNeeded();
		if (openDialog) showDialog(getCollectionElementView());	
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
		try {
			addActions(getCollectionElementView().getPreviousCollectionElementAction());
			addActions(getCollectionElementView().getNextCollectionElementAction());
		} catch (ElementNotFoundException e) {
			log.error(XavaResources.getString("next_previous_action_not_added"), e);
		}
	}
		
	public int getRow() {
		return row;
	}

	public void setRow(int i) {
		row = i;
	}

	public int getNextValue() {
		return nextValue;
	}

	public void setNextValue(int nextValue) {
		this.nextValue = nextValue;
	}

	public boolean isOpenDialog() {
		return openDialog;
	}

	public void setOpenDialog(boolean openDialog) {
		this.openDialog = openDialog;
	}

	/**
	 * @since 7.7
	 */
	protected void setViewNameForEditIfNeeded() {
		MetaCollectionView metaCollectionView = getMetaCollectionView();
		if (metaCollectionView == null) return;
		String editViewName = metaCollectionView.getEditViewName();
		if (!Is.emptyString(editViewName)) {
			getCollectionElementView().setViewName(editViewName);
		} else if (!Is.emptyString(metaCollectionView.getNewViewName())) {
			getCollectionElementView().setViewName(metaCollectionView.getViewName());
		}
	}
	
	private void validRowAndUpdate(int actualRow, int size) {
		row = actualRow + nextValue;
		if (row == -1) {
			addError("at_list_begin");
			row = 0;
		}
		if (row == size) {
			addError("no_list_elements");
			row = size-1;
		}
		getCollectionElementView().setCollectionEditingRow(row);
	}
}
