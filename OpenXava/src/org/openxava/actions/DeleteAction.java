package org.openxava.actions;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.model.*;
import org.openxava.tab.impl.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */

public class DeleteAction extends ViewDetailAction {
	private static Log log = LogFactory.getLog(DeleteAction.class);
	
	public DeleteAction() {
		setIncrement(0);
	}
	
	public void execute() throws Exception {
		if (getView().isKeyEditable()) {
			addError("no_delete_not_exists");
			return;
		}
		Map keyValues = getView().getKeyValues();
		MapFacade.remove(getModelName(), keyValues);
		commit(); // If we change this, we should run all test suite using READ COMMITED (with hsqldb 2 for example)
		resetDescriptionsCache();
		addMessage("object_deleted", getModelName());
		getView().clear();
		boolean selected = false;
		if (getTab().hasSelected()) {
			Map k = calculateNextKey(keyValues);
			if (k == null) setDeleteAllSelected(true);
			else setNextKey(k);
			removeSelected(keyValues);
			selected = true;
		}
		else getTab().cutOutRow(keyValues); 
		super.execute(); 
		if (isNoElementsInList()) {
			if (
				(!selected && getTab().getTotalSize() > 0) ||
				(selected && getTab().getSelectedKeys().length > 0)
			) {				
				setIncrement(-1);
				getErrors().remove("no_list_elements");								
				super.execute();													
			}
			else {							
				getView().setKeyEditable(false);
				getView().setEditable(false);
			}
		}
		getErrors().clearAndClose(); // If removal is done, any additional error message may be confused
	}
	
	private void removeSelected(Map keyValues) throws XavaException {
		getTab().deselect(keyValues);
	}

	private Map calculateNextKey(Map keyValues){
		// By default on deleting we take the next one, if there is not next one we take the previous one, if not we return null
		List l = Arrays.asList(getTab().getSelectedKeys());
		int index = l.indexOf(keyValues);
		int count = l.size();
		
		if (count == 1) return null;	// There is just one element and we're going to delete it
		else if (count-1 == index) return (Map) l.get(index - 1);	// We are in the last element of the list so we take the previous one
		else return (Map) l.get(index + 1);	// We take the next one	
	}
	
}


