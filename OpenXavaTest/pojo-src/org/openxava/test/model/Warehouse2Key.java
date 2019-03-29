package org.openxava.test.model;

import javax.persistence.*;

/**
 *
 * @author Javier Paniza
 */

@Embeddable
public class Warehouse2Key implements java.io.Serializable {
	
	
	@Column(length=3, name="ZONE")	
	private int zoneNumber;	
	
	@Column(length=3, columnDefinition="NUMERIC NOT NULL") // NUMERIC in order to test a fix
	private int number;
	
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
