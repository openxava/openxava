package org.openxava.actions;

import org.openxava.model.meta.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * 
 * @author Javier Paniza 
 */

abstract public class OnChangeMyReportColumnBaseAction extends TabBaseAction implements IOnChangePropertyAction {
	
	public final static String STRING_COMPARATOR = "__STRING__";  
	public final static String DATE_COMPARATOR = "__DATE__";
	public final static String EMPTY_COMPARATOR = "__EMPTY__";
	public final static String OTHER_COMPARATOR = "__OTHER__";	
		
	private Object newValue;
	
	protected Object getNewValue() {
		return newValue;
	}
	
	public void setNewValue(Object value) {
		newValue = value;		
	}
	

	protected void showBooleanValue() {
		getView().setHidden("comparator", true);
		getView().setHidden("value", true);
		getView().setHidden("dateValue", true); 
		getView().setHidden("descriptionsListValue", true); 
		getView().setHidden("booleanValue", false);
		getView().setHidden("validValuesValue", true);
		getView().setHidden("order", false);
	}
	
	protected void showValidValuesValue() {
		getView().setHidden("comparator", true);
		getView().setHidden("value", true);
		getView().setHidden("dateValue", true); 
		getView().setHidden("descriptionsListValue", true); 
		getView().setHidden("booleanValue", true);
		getView().setHidden("validValuesValue", false);
		getView().setHidden("order", false);
	}
	
	protected void showDateValue() { 
		getView().setHidden("comparator", false);
		getView().setHidden("value", true);
		getView().setHidden("dateValue", false); 
		getView().setHidden("descriptionsListValue", true); 
		getView().setHidden("booleanValue", true);
		getView().setHidden("validValuesValue", true);
		getView().setHidden("order", false);
	}	
	
	protected void showDescriptionsListValue() { 
		getView().setHidden("comparator", true);
		getView().setHidden("value", true);
		getView().setHidden("dateValue", true); 
		getView().setHidden("descriptionsListValue", false); 
		getView().setHidden("booleanValue", true);
		getView().setHidden("validValuesValue", true);
		getView().setHidden("order", false);
	}	

	protected void hideMembers() {
		getView().setHidden("comparator", true);
		getView().setHidden("value", true);
		getView().setHidden("dateValue", true); 
		getView().setHidden("descriptionsListValue", true); 
		getView().setHidden("booleanValue", true);
		getView().setHidden("validValuesValue", true);
		getView().setHidden("order", true);
		getView().setHidden("sum", true); 
	}

	protected void showStandardMembers() {
		getView().setHidden("comparator", false);
		getView().setHidden("value", false);			
		getView().setHidden("dateValue", true); 
		getView().setHidden("descriptionsListValue", true); 
		getView().setHidden("booleanValue", true);
		getView().setHidden("validValuesValue", true);
		getView().setHidden("order", false);
	}

	public void setChangedProperty(String propertyName) {		
	}

}
