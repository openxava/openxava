package org.openxava.actions;

import java.util.*;

import org.openxava.model.*;

/**
 * The default action to delete an element in a @OneToMany/@ManyToMany collection.
 * 
 * @author Chungyen Tsai
 *
 */
public class DeleteSelectedInCollectionAction extends DeleteSelectedInCollectionBaseAction {
	
	/**
	 * Is called for each selected row with the values that includes the key
	 * values. <p>
	 */
	protected void removeElement(Map values) throws Exception {
		if (getCollectionElementView().getMetaCollection().isAggregate()) {
			MapFacade.removeCollectionElement(getCollectionElementView().getParent().getModelName(), getCollectionElementView().getParent().getKeyValues(), getCollectionElementView().getMemberName(), values);
		} else {
			MapFacade.remove(getCollectionElementView().getModelName(), values);
		}
	}
	
	protected void addMessage() { 
		addMessage("aggregate_removed", getCollectionElementView().getModelName());
	}
		
}