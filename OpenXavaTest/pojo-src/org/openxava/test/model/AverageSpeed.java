package org.openxava.test.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
// DO NOT ADD @Tab we use this class to test default properties without @Tab
public class AverageSpeed {
	
	@Id @GeneratedValue(generator="system-uuid") @Hidden 
	@GenericGenerator(name="system-uuid", strategy="uuid")
	private String oid;
	
	@ReferenceView("Simple")		
	@SearchKey	
	@ManyToOne(optional=false) 
	private Driver driver;

	@ReferenceView("Simple")		 
	@SearchKey		
	@ManyToOne(optional=false)
	private Vehicle vehicle;
		
	@Column(length=3) @Required
	private int speed;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver drive) {
		this.driver = drive;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
}
