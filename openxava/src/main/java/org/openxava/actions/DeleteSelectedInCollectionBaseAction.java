package org.openxava.actions;

import java.util.*;

import org.openxava.validators.*;

public abstract class DeleteSelectedInCollectionBaseAction extends CollectionBaseAction {
	
	public void execute() throws Exception {
		try{
			Map [] selectedOnes = getSelectedKeys(); 
			validateMinimum(selectedOnes.length); 
			if (selectedOnes.length > 0) {
				for (Map values: selectedOnes) {
					removeElement(values);					
				}				
				commit(); // If we change this, we should run all test suite using READ COMMITED (with hsqldb 2 for example)				
				addMessage();
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
	protected abstract void removeElement(Map values) throws Exception;
	
	protected abstract void addMessage();
		
}