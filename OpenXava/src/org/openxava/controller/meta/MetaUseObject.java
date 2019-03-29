package org.openxava.controller.meta;



import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class MetaUseObject {
	
	private String name;
	private String actionProperty;		
	
	public String getName() {
		return name;
	}

	/**
	 * If is not set it assumes the object name without prefix. <p>
	 * 
	 * For example, if the name is 'view' it assumes 'view' and 
	 * if it's 'xava_view' it assumes 'view'.
	 */
	public String getActionProperty() {
		if (!Is.emptyString(actionProperty)) return actionProperty; 
		if (name==null) return "";
		int dotIdx = name.indexOf('_'); 
		if (dotIdx >= 0) {
			return name.substring(dotIdx+1); 
		}
		else {
			return name;
		}
	}

	public void setName(String string) {
		name = string;
	}

	public void setActionProperty(String string) {
		actionProperty = string;
	}

}
