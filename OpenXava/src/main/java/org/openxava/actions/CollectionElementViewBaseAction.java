package org.openxava.actions;

import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.validators.ValidationException;
import org.openxava.view.*;
import org.openxava.view.meta.*;

/**
 * 
 * @author Javier Paniza
 */

abstract public class CollectionElementViewBaseAction extends ViewBaseAction { 
	

	
	private View collectionElementView;		
	private String viewObject;
	private boolean closeDialogDisallowed = false;
	private boolean dialogOpened = false; 

	abstract public void execute() throws Exception;
	
	public View getView() { 
		if (viewObject != null && !dialogOpened) return super.getView();		
		return getCollectionElementView().getRoot();		
	}
	
	protected boolean mustRefreshCollection() { 
		return getManager().isExecutingAction(); // So false when we are determining if the action is available 
	}
		
	protected void showDialog(View viewToShowInDialog) throws Exception {
		super.showDialog(viewToShowInDialog);
		dialogOpened = true;
		collectionElementView = null; 
	}
		
	/** @since 4m5 */
	protected View getParentView() throws XavaException {
		return getCollectionElementView().getParent();
	}
	
	protected View getCollectionElementView() throws XavaException {
		if (collectionElementView == null) {
			if (viewObject == null || dialogOpened) collectionElementView = super.getView(); // In a dialog
			else {
				collectionElementView = (View) getContext().get(getRequest(), viewObject);
			}
			if (mustRefreshCollection()) collectionElementView.refreshCollections(); 			
		}
		return collectionElementView;
	}
	
	/**@Since 4.9.1*/ 
	protected MetaCollection getMetaCollection() throws ElementNotFoundException, XavaException {
		return getCollectionElementView().getParent().getMetaModel().getMetaCollection(getCollectionElementView().getMemberName());
	}
	
	protected void validateMinimum(int elementsToRemove) throws ValidationException, XavaException {
		MetaCollection metaCollection = getMetaCollection();
		int minimum = metaCollection.getMinimum();
		if(minimum > 0) {
			if(getCollectionElementView().getCollectionSize() - elementsToRemove < minimum) {
				Messages errors = new Messages();
				String elements = XavaResources.getString(minimum == 1?"element":"elements");
				errors.add("minimum_elements", new Integer(minimum), "'" + elements + "'",	metaCollection.getName(), metaCollection.getMetaModel().getName());
				throw new ValidationException(errors);
			}
		}
	}
	
	protected void validateMaximum(int elementsToAdd) throws ValidationException, XavaException {
		MetaCollection metaCollection = getMetaCollection();
		int maximum = metaCollection.getMaximum();
		if(maximum > 0) { 
			if(getCollectionElementView().getCollectionSize() + elementsToAdd > maximum) {
				Messages errors = new Messages();
				errors.add("maximum_elements", new Integer(maximum), metaCollection.getName(), metaCollection.getMetaModel().getName());
				throw new ValidationException(errors);
			}	
		}
	}
	
	protected boolean isEntityReferencesCollection() throws XavaException {
		return isEntityReferencesCollection(getCollectionElementView()); 
	}
	
	/**
	 * @since 6.2.1
	 */
	protected boolean isEntityReferencesCollection(View view) throws XavaException { 
		if (!view.isRepresentsCollection()) return false;
		MetaCollectionView metaCollectionView = view.getMetaView().getMetaCollectionView(getMetaCollection().getName());
		if (metaCollectionView != null) return !metaCollectionView.isAsAggregate();
		return view.getMetaModel() instanceof MetaEntity; 		
	}

	
	public String getViewObject() {
		return viewObject;
	}

	public void setViewObject(String viewObject) {
		this.viewObject = viewObject;
	}

	@Override
	protected void closeDialog() {  
		if (isCloseDialogDisallowed()) {
			getCollectionElementView().reset();
		} else {
			super.closeDialog();
			dialogOpened = false;
			collectionElementView = null; 
		}
	}	
	
	public void setCloseDialogDisallowed(boolean closeDialogDisallowed) {
		this.closeDialogDisallowed = closeDialogDisallowed;
	}

	public boolean isCloseDialogDisallowed() {
		return closeDialogDisallowed;
	}
	
}
