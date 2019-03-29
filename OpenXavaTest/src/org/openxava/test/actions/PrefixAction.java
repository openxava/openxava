package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.view.*;

/**
 * An example of IPropertyAction. <p>
 * 
 * @author Javier Paniza
 */

public class PrefixAction extends BaseAction implements IPropertyAction {
	
	private String prefix;
	private View view;
	private String property;

	public void execute() throws Exception {		
		String street = view.getValueString(property); 		
		view.setValue(property, prefix + street); 		
	}

	public void setProperty(String propertyName) {
		this.property = propertyName;		
	}

	public void setView(View view) {
		this.view = view;		
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}	

}
