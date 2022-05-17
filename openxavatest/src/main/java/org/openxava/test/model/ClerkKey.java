package org.openxava.test.model;

import javax.persistence.*;

/**
 * @author Javier Paniza
 */
public class ClerkKey implements java.io.Serializable {

	@Column(name="ZONE")  
	private int zoneNumber;
	@Column(name="OFFICE")
	private int officeNumber;
	private int number;
	

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
		return "ClerkKey::" + zoneNumber+ ":" + officeNumber + ":" + number;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getOfficeNumber() {
		return officeNumber;
	}

	public void setOfficeNumber(int officeNumber) {
		this.officeNumber = officeNumber;
	}

	public int getZoneNumber() {
		return zoneNumber;
	}

	public void setZoneNumber(int zoneNumber) {
		this.zoneNumber = zoneNumber;
	}


}
