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
		
	/** @since 4m5
	* @throws XavaException
	 */
	protected View getParentView() {
		return getCollectionElementView().getParent();
	}
	
	/**
	* @throws XavaException
	 */
	protected View getCollectionElementView() {
		if (collectionElementView == null) {
			if (viewObject == null || dialogOpened) collectionElementView = super.getView(); // In a dialog
			else {
				collectionElementView = (View) getContext().get(getRequest(), viewObject);
			}
			if (mustRefreshCollection()) collectionElementView.refreshCollections(); 			
		}
		return collectionElementView;
	}
	
	/**@Since 4.9.1
	* @throws XavaException
	 */
	protected MetaCollection getMetaCollection() throws ElementNotFoundException {
		return getCollectionElementView().getParent().getMetaModel().getMetaCollection(getCollectionElementView().getMemberName());
	}
	
	/**
	* @throws XavaException
	 */
	protected void validateMinimum(int elementsToRemove) throws ValidationException {
		MetaCollection metaCollection = getMetaCollection();
		int minimum = metaCollection.getMinimum();
		if(minimum > 0) {
			if(getCollectionElementView().getCollectionSize() - elementsToRemove < minimum) {
				Messages errors = new Messages();
				String elements = XavaResources.getString(minimum == 1?"element":"elements");
				errors.add("minimum_elements", Integer.valueOf(minimum), "'" + elements + "'",	metaCollection.getName(), metaCollection.getMetaModel().getName());
				throw new ValidationException(errors);
			}
		}
	}
	
	/**
	* @throws XavaException
	 */
	protected void validateMaximum(int elementsToAdd) throws ValidationException {
		MetaCollection metaCollection = getMetaCollection();
		int maximum = metaCollection.getMaximum();
		if(maximum > 0) { 
			if(getCollectionElementView().getCollectionSize() + elementsToAdd > maximum) {
				Messages errors = new Messages();
				errors.add("maximum_elements", Integer.valueOf(maximum), metaCollection.getName(), metaCollection.getMetaModel().getName());
				throw new ValidationException(errors);
			}	
		}
	}
	
	/**
	* @throws XavaException
	 */
	protected boolean isEntityReferencesCollection() {
		return isEntityReferencesCollection(getCollectionElementView()); 
	}
	
	/**
	 * @since 6.2.1
	 * @throws XavaException
	 */
	protected boolean isEntityReferencesCollection(View view) { 
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
			getCollectionElementView().setCollectionDetailVisible(false);
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
	
	/**
	 * @since 7.7
	 */
	protected MetaCollectionView getMetaCollectionView() {
		try {
			return getCollectionElementView().getParent().getMetaView().getMetaCollectionView(getCollectionElementView().getMemberName());
		}
		catch (Exception ex) {
			return null;
		}
	}
	
	/**
	 * @since 7.6.3
	 */
	protected String getCollectionLabel() {
		String parentModel = getCollectionElementView().getParent().getModelName();
		String memberName = getCollectionElementView().getMemberName();
		return Labels.get(parentModel + "." + memberName);
	}
	
}
