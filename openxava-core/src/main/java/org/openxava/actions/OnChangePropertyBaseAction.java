package org.openxava.actions;



import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * @author Javier Paniza
 */

abstract public class OnChangePropertyBaseAction
	extends ViewBaseAction 
	implements IOnChangePropertyAction {
		
	private String changedProperty;
	private Object newValue;
	
	public Object getNewValue() {
		return newValue;
	}

	public String getChangedProperty() {
		return changedProperty;
	}
	
	protected MetaProperty getChangedMetaProperty() throws XavaException { 
		return getView().getMetaProperty(changedProperty);
	}
	
	/**
	 * The view where the on-change is declared. <p>
	 * 
	 * This may be the main view or the module (if property-view : on-change
	 * is declared in main view) or an subview (if it's declared inside a
	 * aggregate view, for example). 
	 * 
	 * @return
	 */
	public View getView() {
		return super.getView(); 
	}

	public void setNewValue(Object object) {
		newValue = object;
	}

	public void setChangedProperty(String string) {
		changedProperty = string;
	}

	protected void showView(View newView) {
		super.showView(newView);
		getContext().put(getManager().getApplicationName(), getManager().getModuleName(), "xava_view", newView);
	}
	
}
