package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;


/**
 * To test AsserTrue message on a method
 * 
 * @author Jeromy Altuna
 */

@Entity
public class MotorVehicle {
	
	@Hidden
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
	private int id;
	
	@Required
	@Column(length = 15)
	private String type;
	
	@Required
	@Column(length = 7)
	private String licensePlate;
	
	private boolean roadworthy;
	
	@ManyToOne
	private MotorVehicleDriver driver;
	
	@javax.validation.constraints.AssertTrue(message="{not_roadworthy}")
	private boolean isRoadworthyToAssignTheDriver(){
		return getDriver() == null || isRoadworthy();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public boolean isRoadworthy() {
		return roadworthy;
	}

	public void setRoadworthy(boolean roadworthy) {
		this.roadworthy = roadworthy;
	}

	public MotorVehicleDriver getDriver() {
		return driver;
	}

	public void setDriver(MotorVehicleDriver driver) {
		this.driver = driver;
	} 				
}
