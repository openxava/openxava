package org.openxava.actions;

import java.util.*;

import org.openxava.model.*;

/**
 * Save a modified ManyToMany element.
 * <p>
 * 
 * @author Franklin Alier
 */
public class UpdateElementInManyToManyCollectionAction extends CollectionElementViewBaseAction {
	
	public void execute() throws Exception {
		Map keyValues = getCollectionElementView().getKeyValues();
		MapFacade.setValues(getCollectionElementView().getModelName(),
				keyValues, getCollectionElementView().getValues());
		addMessage("entity_modified", getCollectionElementView().getModelName());
		getPreviousView().refreshCollections();
		closeDialog();
	}
	
}
