package org.openxava.actions;

import java.util.*;

import org.openxava.util.*;
import org.openxava.web.editors.*;

/**
 * Base class for  AttachedFileEditor actions in default-controllers.xml. <p>
 * 
 * @since 6.5.1
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
