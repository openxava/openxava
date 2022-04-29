package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * To test AsserTrue message on a field
 * 
 * @author Jeromy Altuna
 */

@Entity
@Tab(baseCondition="e.class = MotorVehicleDriver")
public class MotorVehicleDriver extends Identifiable {
	
	@Required
	@Column(length = 40)
	private String name;
	
	@javax.validation.constraints.AssertTrue(
		message = "{disapproved_driving_test}")
	private boolean approvedDrivingTest;
	
	@OneToMany(mappedBy = "driver")	
	private Collection<MotorVehicle> vehicles = new ArrayList<MotorVehicle>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isApprovedDrivingTest() {
		return approvedDrivingTest;
	}

	public void setApprovedDrivingTest(boolean approvedDrivingTest) {
		this.approvedDrivingTest = approvedDrivingTest;
	}

	public Collection<MotorVehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(Collection<MotorVehicle> vehicles) {
		this.vehicles = vehicles;
	}
		
}
