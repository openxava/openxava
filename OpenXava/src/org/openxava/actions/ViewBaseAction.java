package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.apache.commons.logging.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.validators.*;
import org.openxava.view.*;
import org.openxava.view.meta.*;

/**
 * 
 * @author Javier Paniza
 */

abstract public class ViewBaseAction extends BaseAction { 
	
	private static Log log = LogFactory.getLog(ViewBaseAction.class);
		
	@Inject 
	private View view;
	@Inject
	private Stack previousViews;
	private boolean dialogShown = false; 
	private boolean hasNextControllers = false; 
		
	/**
	 * Creates a new view and shows it. <p>
	 * 
	 * After it if you call to getView() it will return this new view.<br>
	 * 
	 * @since 4m1
	 */
	protected void showNewView() { 
		showView(new View());			
	}
	
	/**
	 * Shows the specified view. <p>
	 * 
	 * After it if you call to getView() it will be the specified view.<br>
	 * 
	 * @since 4m2
	 */	
	protected void showView(View newView) {  
		getView().putObject("xava.mode", getManager().getModeName());	
		newView.setRequest(getRequest());
		newView.setErrors(getErrors()); 
		newView.setMessages(getMessages());
		getPreviousViews().push(getView());
		setView(newView);		
		setNextMode(DETAIL);
	}
	
	/**
	 * Shows the specified view inside a dialog. <p>
	 * 
	 * After it if you call to getView() it will be the specified view.<br>
	 * 
	 * @since 4m2
	 */		
	protected void showDialog(View viewToShowInDialog) throws Exception { 
		showView(viewToShowInDialog);
		
		if (!isControllersChanged()) {
			clearActions();
		}
		
		getManager().showDialog();
		dialogShown = true;
	}

	private boolean isControllersChanged() throws Exception { 
		if (hasNextControllers) return true;
		if (!(this instanceof IChangeControllersAction)) return false;
		return ((IChangeControllersAction) this).getNextControllers() != null;
	}
	
	
	/**
	 * Creates a new view and shows it inside a dialog. <p>
	 * 
	 * After it if you call to getView() it will return this new view.<br>
	 * 
	 * @since 4m2
	 */	
	protected void showDialog() throws Exception { 
		showDialog(new View());
	}
	
	/**
	 * @since 4m2
	 */
	protected void closeDialog() { 
		returnToPreviousView();
		if (!dialogShown) returnToPreviousControllers(); 
		getManager().closeDialog();
		dialogShown = false;
	}	
	
	/**
	 * @since 4m1
	 */	
	protected void returnToPreviousView() {		
		if (getPreviousViews() != null) {
			if (!getPreviousViews().empty()) {			
				View previousView = (View) getPreviousViews().pop();
				previousView.setRequest(getRequest());
				setView(previousView);
				setNextMode((String) getView().getObject("xava.mode"));				
			}
			else {
				log.warn(XavaResources.getString("no_more_previous_views")); 
			}
		}
		else {
			log.warn(XavaResources.getString( 
				"use_object_previousViews_required", "returnToPreviousView()", getClass().getName())); 
		}
	}
	
	/**
	 * @since 5.6
	 */
	protected void validateViewValues() { 
		String containerReference = null;
		if (getView().isRepresentsCollection()) {
			containerReference = getView().getMetaCollection().getMetaReference().getRole();
		}
		Messages errors = MapFacade.validateIncludingMissingRequired(getView().getModelName(), getView().getValues(), containerReference);
		if (errors.contains()) throw new ValidationException(errors);
	}
	
	
	/**
	 * @since 4m1
	 */	
	protected View getPreviousView() {
		return getPreviousViews().peek();					
	}
		
	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}
			
	protected String getModelName() {
		return getView().getModelName(); 
	}
	
	/**
	 * Reset the cache of all descriptions-list and 
	 * others uses of descriptionsEditors.	 
	 */
	protected void resetDescriptionsCache() {
		super.resetDescriptionsCache();
		getView().refreshDescriptionsLists();
	}
			
	public Stack<View> getPreviousViews() { 
		return previousViews;
	}

	public void setPreviousViews(Stack previousViews) {
		this.previousViews = previousViews;
	}
	
	
	protected void setControllers(String... controllers) {
		if (dialogShown) getManager().setControllersNames(controllers);
		else super.setControllers(controllers);		
		hasNextControllers = controllers != null; 
	}
	
}
