package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.apache.commons.logging.*;
import org.openxava.model.meta.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.validators.*;
import org.openxava.view.*;
import org.openxava.web.*;

/**
 * @since 5.9
 * @author Javier Paniza
 */
public class CutElementsFromCollectionAction extends CollectionBaseAction implements IAvailableAction, IJavaScriptPostAction {
	
	private static Log log = LogFactory.getLog(CutElementsFromCollectionAction.class);
	
	@Inject
	private CutCollectionElements cutCollectionElements;

	public void execute() throws Exception {
		try{
			log.info("getCollectionElementView().getModelName()>" + getCollectionElementView().getModelName()); // Temporal log to figure out this bug: https://openxava.org/xavaprojects/o/OpenXava/m/Issue?detail=ff80808197a7d1f20197b130db820015
			log.info("getCollectionElementView().getParent().getModelName()>" + getCollectionElementView().getParent().getModelName()); // Temporal log to figure out this bug: https://openxava.org/xavaprojects/o/OpenXava/m/Issue?detail=ff80808197a7d1f20197b130db820015
			Map [] selectedOnes = getSelectedKeys();
			validateMinimum(selectedOnes.length); 
			if (selectedOnes.length > 0){ 
				cutCollectionElements.clear();
				for (Map values: selectedOnes){
					cutCollectionElements.addElement(values);
				}				
				cutCollectionElements.setParentModel(getCollectionElementView().getParent().getModelName());
				cutCollectionElements.setParentKey(getCollectionElementView().getParent().getKeyValues());
				cutCollectionElements.setCollectionName(getCollectionElementView().getMemberName());
				cutCollectionElements.setCollectionModel(getCollectionElementView().getMetaModel().getName());
				int count = cutCollectionElements.elementsCount();
				String collectionLabel = getCollectionLabel();
				addMessage(count > 1?"elements_cut_from_collection":"element_cut_from_collection", count, collectionLabel);
				if (count > 0) refreshOtherCollectionsOfSameType();
			}
			else {
				addWarning("no_elements_to_cut");
			}
			log.info("getCollectionElementView().getModelName()<" + getCollectionElementView().getModelName()); // Temporal log to figure out this bug: https://openxava.org/xavaprojects/o/OpenXava/m/Issue?detail=ff80808197a7d1f20197b130db820015
			log.info("getCollectionElementView().getParent().getModelName()<" + getCollectionElementView().getParent().getModelName()); // Temporal log to figure out this bug: https://openxava.org/xavaprojects/o/OpenXava/m/Issue?detail=ff80808197a7d1f20197b130db820015			
		}
		catch (ValidationException ex) {			
			addErrors(ex.getErrors());
		}
	}
	
	private void refreshOtherCollectionsOfSameType() {
		for (MetaCollection collection: getView().getMetaModel().getMetaCollections()) {
			if (collection.getMetaReference().getMetaModelReferenced().getName().equals(getCollectionElementView().getMetaModel().getName())) {
				try {
					View collectionView = getView().getSubview(collection.getName());
					if (collectionView != getCollectionElementView()) {
						collectionView.refreshCollections();
					}
				}
				catch (ElementNotFoundException ex) {
				}
			}
		}
	}

	protected boolean mustRefreshCollection() { 
		return false;
	}

	public boolean isAvailable() {
		return getCollectionElementView().isCollectionEditable();
	}

	public String getPostJavaScript() {
		String collectionName = getCollectionElementView().getMemberName();
		String collectionId = Ids.decorate(getRequest(), collectionName);
		String rowId = "null";
		if (getRow() >= 0) {
			String rowIdPrefix = getCollectionElementView().isCollectionFromModel()?collectionName + "___":"xava_collectionTab_" + collectionName + "_";
			rowId = "'" + Ids.decorate(getRequest(), rowIdPrefix + getRow()) + "'";
		}
		return "javascript:openxava.markRowAsCut('" + collectionId + "', " + rowId + ")";
	}

}
