package org.openxava.actions;

import org.openxava.util.*;

/**
 * 
 * Created on 04/02/2009 (16:23:01)
 * @autor Ana Andres
 */
public class SetPropertyToNullAction extends ViewBaseAction { 
	
	private String property; 
	
	public void execute() throws Exception {
		try {
			getView().setValue(property, null);
		}
		catch (ElementNotFoundException ex) { 
			// For @ElementCollection
		}
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
