package org.openxava.calculators;

import java.rmi.*;

import org.openxava.util.*;

/**
 * Return the value of the indicated property of the sent object model. <p>
 * 
 * Useful for accessing to a calculated property of a handmade POJO.<br>
 * 
 * @author Javier Paniza
 */

public class ModelPropertyCalculator implements IModelCalculator {
	
	private Object model;
	private String property;
	private Object valueOfDependsProperty; 

	public void setModel(Object model) throws RemoteException {
		this.model = model;		
	}

	public Object calculate() throws Exception {
		PropertiesManager pm = new PropertiesManager(model); 		
		return pm.executeGet(property);
		
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	public Object getValueOfDependsProperty() {
		return valueOfDependsProperty;
	}

	public void setValueOfDependsProperty(Object valueOfDependsProperty) {
		this.valueOfDependsProperty = valueOfDependsProperty;
	}

}
