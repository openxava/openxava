package org.openxava.view.meta;

import java.io.*;
import java.util.*;




/**
 * @author Javier Paniza
 */
public class MetaSearchAction implements Serializable, Cloneable {
	
	private String className = "puntocom.xava.xvista.swing.AccionBuscar"; // only for spanish/swing version

	private Map propertiesValues;
	private String actionName;
	
	
		

	/**
	 * Only for spanish/swing version.
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * Only for spanish/swing version.
	 */	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public Map getPropertiesValues() {		
		return propertiesValues==null?new HashMap():propertiesValues;				
	}

	public void addPropertyValue(String name, String value) {
		if (propertiesValues == null) {
			propertiesValues = new HashMap();
		}
		propertiesValues.put(name, value);
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String string) {
		actionName = string;
	}
	
	public Object clone() throws CloneNotSupportedException { 
		MetaSearchAction clon = (MetaSearchAction) super.clone();
		if (propertiesValues != null) clon.propertiesValues = new HashMap(propertiesValues);
		return clon;
	}

}
