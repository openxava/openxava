package org.openxava.test.actions;

import org.openxava.actions.*;




/**
 * @author Javier Paniza
 */
public class SetPropertyValueAction extends ViewBaseAction {

	private String value;
	private String property;

	public void execute() throws Exception {
		getView().setValue(property, value);		
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String string) {
		property = string;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String string) {
		value = string;
	}

}
