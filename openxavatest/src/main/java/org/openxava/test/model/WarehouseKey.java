package org.openxava.test.model;

import java.io.*;

import javax.persistence.*;


/**
 * 
 * @author Javier Paniza
 */

public class WarehouseKey implements Serializable {
	
	@Column(name="ZONE")
	private int zoneNumber;
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
		return "WarehouseKey::" + zoneNumber+ ":" + number;
	}
		
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getZoneNumber() {
		return zoneNumber;
	}

	public void setZoneNumber(int zoneNumber) {
		this.zoneNumber = zoneNumber;
	}
	
}
