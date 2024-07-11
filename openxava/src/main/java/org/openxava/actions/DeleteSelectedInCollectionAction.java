package org.openxava.actions;

import java.util.*;

import org.openxava.model.*;
import org.openxava.validators.*;

public class DeleteSelectedInCollectionAction extends CollectionBaseAction {
	
	public void execute() throws Exception {
		try{
			Map [] selectedOnes = getSelectedKeys();
			validateMinimum(selectedOnes.length);
			if (selectedOnes.length > 0) {
				for (Map values: selectedOnes) {
					removeElement(values);
				}				
				commit(); // If we change this, we should run all test suite using READ COMMITED (with hsqldb 2 for example)				
				addMessage("aggregate_removed", getCollectionElementView().getModelName());
				getView().recalculateProperties();
				getCollectionElementView().collectionDeselectAll();
				getCollectionElementView().refreshCollections(); 
			}
		}
		catch (ValidationException ex) {
			addErrors(ex.getErrors());
		}
	}
	
	/**
	 * Is called for each selected row with the values that includes the key
	 * values. <p>
	 */
	protected void removeElement(Map values) throws Exception {
		MapFacade.remove(getCollectionElementView().getModelName(), values);
	}
		
}