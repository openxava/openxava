package org.openxava.actions;



import org.openxava.model.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 * @author Jeromy Altuna
 */

public class RemoveElementFromCollectionAction extends CollectionElementViewBaseAction {
	
	
	
	public void execute() throws Exception {
		try {											
			if (!getCollectionElementView().getKeyValuesWithValue().isEmpty()) {
				validateMinimum(1);
				MapFacade.removeCollectionElement(getCollectionElementView().getParent().getModelName(), getCollectionElementView().getParent().getKeyValues(), getCollectionElementView().getMemberName(), getCollectionElementView().getKeyValues());
				commit(); // If we change this, we should run all test suite using READ COMMITED (with hsqldb 2 for example) 
				if (isEntityReferencesCollection()) {
					addMessage("association_removed", getCollectionElementView().getModelName(), getCollectionElementView().getParent().getModelName());
				}
				else {
					addMessage("aggregate_removed", getCollectionElementView().getModelName());
				}
			}			
			getCollectionElementView().setCollectionEditingRow(-1);
			getCollectionElementView().clear();
			getView().recalculateProperties();
			closeDialog(); 
		}
		catch (ValidationException ex) {			
			addErrors(ex.getErrors());
		}				
	}
	
}
