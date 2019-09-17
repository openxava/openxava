package org.openxava.actions;


/**
 * 
 * Created on 04/02/2009 (16:23:01)
 * @autor Ana Andres
 */
public class SetPropertyToNullAction extends ViewBaseAction { 
	
	private String property; 
	
	public void execute() throws Exception {
		getView().setValue(property, null); 
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
