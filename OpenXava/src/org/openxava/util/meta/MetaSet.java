package org.openxava.util.meta;

import java.io.*;



import org.openxava.util.*;


/**
 * @author Javier Paniza
 */
public class MetaSet implements Serializable {
	
	private String propertyName;
	private String propertyNameFrom;
	private String value;
	
	

	public String getPropertyName() {
		return propertyName;
	}

	public String getPropertyNameFrom() {
		if (hasValue()) return ""; // value an property from are not compatibles
		if (Is.emptyString(propertyNameFrom)) return getPropertyName();		
		return propertyNameFrom;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public void setPropertyNameFrom(String propertyNameFrom) {
		this.propertyNameFrom = propertyNameFrom;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean hasValue() {
		// No trim because spaces are valid values
		return !(value == null || value.length() == 0);
	}

}
