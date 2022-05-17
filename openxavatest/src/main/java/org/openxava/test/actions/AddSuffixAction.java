package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class AddSuffixAction extends ViewBaseAction {
	
	private String property;
	private String suffix;
	
	public void execute() throws Exception {
		String value = getView().getValueString(property);
		getView().setValue(property, value.toLowerCase() + suffix);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
