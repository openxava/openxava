package org.openxava.actions;

import java.util.*;

import javax.ejb.*;
import javax.inject.*;
import org.apache.commons.logging.*;
import org.openxava.tab.*;
import org.openxava.util.*;

/**
 * This is for the case of collections of entities without @AsEmbedded (or without as-aggregate="true"). <p>
 * 
 * The remaining cases are treated by {@link SaveElementInCollectionAction}.<br> 
 * This case add a group of entities from a list.<br>
 *  
 * @author Javier Paniza
 * @author Jeromy Altuna
 */

public class AddElementsToCollectionAction extends SaveElementInCollectionAction implements INavigationAction {
	
	private static Log log = LogFactory.getLog(AddElementsToCollectionAction.class);
	 		
	@Inject 
	private Tab tab;
	private int added;
	private int failed;
	private int row = -1;
	@Inject
	private String currentCollectionLabel;
	
	public void execute() throws Exception {
		saveIfNotExists(getCollectionElementView().getParent());		
		if (row >= 0) {
			validateMaximum(1); 
			associateEntityInRow(row);
		}
		else {
			Map [] selectedOnes = getTab().getSelectedKeys();
			validateMaximum(selectedOnes.length); 
			if (selectedOnes != null && selectedOnes.length > 0) {						
				for (int i = 0; i < selectedOnes.length; i++) {
					associateKey(selectedOnes[i]);
				}
			}		
			else {
				addError("choose_element_before_add");
				return;
			}
		}
		addMessage("elements_added_to_collection", new Integer(added), currentCollectionLabel);		
		if (failed > 0) addError("elements_not_added_to_collection", new Integer(failed), currentCollectionLabel);
		getView().setKeyEditable(false); // To mark as saved
		getTab().deselectAll();
		getCollectionElementView().refreshCollections(); // To reset collection totals 
		resetDescriptionsCache();
		if (added > 0) {
			getView().recalculateProperties(); 
			closeDialog();
		}
	}

	private void associateEntityInRow(int row) throws FinderException, Exception {
		Map key = (Map) getTab().getTableModel().getObjectAt(row);
		associateKey(key);
	}
	
	private void associateKey(Map key){
		try {									
			associateEntity(key); 					
			added++;
		}
		catch (Exception ex) {
			addValidationMessage(ex); 
			failed++;
			log.error(
				XavaResources.getString("add_collection_element_error", 
						getCollectionElementView().getModelName(), 
						getCollectionElementView().getParent().getModelName()), 
					ex);			
		}
	}
		
	public String getNextAction() throws Exception { 
		// In order to annul the chaining of the action
		return null;
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab web) {
		tab = web;
	}


	public String[] getNextControllers() {		
		return added > 0?PREVIOUS_CONTROLLERS:SAME_CONTROLLERS;
	}

	public String getCustomView() {		
		return added > 0?PREVIOUS_VIEW:SAME_VIEW; 
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
	// This action is executed from an dialog so it has not viewObject,
	// but it could be injected from a old (not updated) action definition 
	// in controllers.xml using use-object. This method is to avoid that 
	// viewObject has value in that case
	public void setViewObject(String viewObject) { 
	}

}
