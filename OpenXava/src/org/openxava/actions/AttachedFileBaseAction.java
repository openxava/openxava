package org.openxava.actions;

import java.util.*;

import org.openxava.util.*;
import org.openxava.web.editors.*;

/**
 * tmp
 * Base class for  AttachedFileEditor actions in default-controllers.xml. <p>
 * 
 * @author Jeromy Altuna
 * @author Javier Paniza
 */
abstract public class AttachedFileBaseAction extends ViewBaseAction {
	
	private String property; 
		
	protected void trackModification(AttachedFile file, String message) {   
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(property, XavaResources.getString(message, file.getName())); 
		Map newChangedValues = new HashMap();
		newChangedValues.put(property, file.getId()); 
		AccessTracker.modified(getView().getModelName(), getView().getKeyValues(), oldChangedValues, newChangedValues); 
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
}
