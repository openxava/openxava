package org.openxava.actions;

import java.util.*;
import javax.inject.*;
import org.apache.commons.logging.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * @since 5.9
 * @author Javier Paniza
 */
public class PasteElementsIntoCollectionAction extends SaveElementInCollectionAction implements IAvailableAction {
	
	private static Log log = LogFactory.getLog(PasteElementsIntoCollectionAction.class); 
	
	private int added;
	private int failed;
		
	@Inject
	private CutCollectionElements cutCollectionElements;
	
	public void execute() throws Exception {
		if (cutCollectionElements.elementsCount() == 0) {
			addError("no_elements_to_paste"); 
			return;
		}
		if (!isSameType()) {
			addError("impossible_paste_in_different_type");
			return;
		}
		
		saveIfNotExists(getCollectionElementView().getParent());		
		validateMaximum(cutCollectionElements.elementsCount()); 
		for (Map element: cutCollectionElements.getElements()) {
			paste(element);		
		}
		String collectionLabel = Labels.get(getCollectionElementView().getMemberName());		
		addMessage(added>1?"elements_pasted_into_collection":"element_pasted_into_collection", 
			new Integer(added), collectionLabel);		
		if (failed > 0) {
			addError(failed>1?"elements_not_pasted_into_collection":"element_not_pasted_into_collection", 
				new Integer(failed), collectionLabel);
		}
		if (added > 0) {
			refreshSourceCollection();
		}
		getView().setKeyEditable(false); // To mark as saved
		getCollectionElementView().refreshCollections(); // To reset collection totals 
		cutCollectionElements.clear();
	}
	
	private void refreshSourceCollection() {
		MetaModel metaModel = getView().getMetaModel();
		if (!metaModel.getName().equals(cutCollectionElements.getParentModel())) return;
		try {
			View sourceCollectionView = getView().getSubview(cutCollectionElements.getCollectionName());
			sourceCollectionView.refreshCollections();
		}
		catch (ElementNotFoundException ex) {
		}
	}

	private void paste(Map element){  
		try {
			MapFacade.moveCollectionElementToAnotherCollection(
				cutCollectionElements.getParentModel(), cutCollectionElements.getParentKey(), cutCollectionElements.getCollectionName(),  
				getCollectionElementView().getParent().getMetaModel().getName(),
				getCollectionElementView().getParent().getKeyValues(),
				getCollectionElementView().getMemberName(), 
				element);
			added++;
		}
		catch (Exception ex) {
			addValidationMessage(ex); 
			failed++;
			log.error(
				XavaResources.getString("paste_element_error",  
						getCollectionElementView().getModelName(), 
						getCollectionElementView().getParent().getModelName()), 
					ex);			
		}
	}

	public boolean isAvailable() {
		if (!getCollectionElementView().isCollectionEditable()) return false;
		return cutCollectionElements.elementsCount() > 0 && isSameType();
	}
	
	private boolean isSameType() {
		return cutCollectionElements.getCollectionModel().equals(getCollectionElementView().getMetaModel().getName());
	}
	
}
