package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */

@MappedSuperclass
public class VehicleDocument extends Identifiable {
	
	@Column(length=40) @Required
	private String description;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Vehicle vehicle;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

}
