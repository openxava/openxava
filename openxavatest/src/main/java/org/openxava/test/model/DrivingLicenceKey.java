package org.openxava.test.model;

import java.io.*;

/**
 * 
 * @author Javier Paniza
 */

public class DrivingLicenceKey implements Serializable {
	
	private String type;
	private int level;
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return obj.toString().equals(this.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "DrivingLicence::" + type + ":" + level;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
