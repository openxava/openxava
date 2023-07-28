package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;


/**
 * To test AsserTrue message on a method
 * 
 * @author Jeromy Altuna
 */

@Entity
@View(name="WithOnChangeRoadworthy", members="roadworthy; type; licensePlate") // roadworthy must be the first one to test a case
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
	
	@OnChange(forViews="WithOnChangeRoadworthy", value=OnChangeVoidAction.class) 
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
