package org.openxava.util;

import java.io.*;




/**
 * A class with a key and a description. <p>
 * 
 * @author Javier Paniza
 */
public class KeyAndDescription implements Serializable {
	boolean showCode=false;
	private Object key;
	private Object description;
	
	
	public KeyAndDescription() {
	}
	
	public KeyAndDescription(Object key, Object description) {
		this.key = key;
		this.description = description;
	}

	public Object getKey() {
		return key;
	}

	public Object getDescription() {
		return description;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public void setDescription(Object description) {
		this.description = description;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof KeyAndDescription)) return false;
		KeyAndDescription o = (KeyAndDescription) other;	
		return Is.equal(this.key, o.key);	
	}
		
	public int hashCode() {
		return key.hashCode();
	}

	public String toString() {
		if (showCode)
			return description==null?"":description.toString().trim() + " [" +key+"]";
		else 
			return description==null?"":description.toString().trim();
	}

	public boolean isShowCode() {
		return showCode;
	}

	public void setShowCode(boolean showCode) {
		this.showCode = showCode;
	}

}
