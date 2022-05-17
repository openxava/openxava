package org.openxava.actions;

import java.util.*;

import org.openxava.util.*;
import org.openxava.view.*;

/**
 * Remove an element in an @ElementCollection. 
 * 
 * This action is not used by default, because OpenXava uses JavaScript to remove a row in an
 * element collection directly in the browser. However, you can use @RemoveSelectedAction to call
 * this action and it works. The goal of this action is to be refined, so the developer could
 * add Java code to the row removal.
 * 
 * @author Javier Paniza
 * @since 5.3.2 
 */

public class RemoveSelectedInElementCollectionAction extends RemoveSelectedInCollectionAction {
	
	private View collectionElementView; 
		
	protected void commit() {
	}
	
	
	/**
	 * Is called for each selected row with the values that includes the key
	 * values. 
	 */
	protected void removeElement(Map values) throws Exception {
		List collectionValues = getCollectionElementView().getCollectionValues();
		int idx = collectionValues.indexOf(values);
		collectionValues.remove(idx);
	}
	
	protected View getCollectionElementView() throws XavaException {
		if (collectionElementView == null) {
			View rootView = (View) getContext().get(getRequest(), "xava_view");
			String collectionName = Strings.lastToken(getViewObject(), "_");
			collectionElementView = rootView.getSubview(collectionName); 
			collectionElementView.refreshCollections();
		}
		return collectionElementView;
	}
	
	protected boolean isEntityReferencesCollection() throws XavaException {
		return false;
	}
	
	protected void addMessage() { 		
	}
		
}