package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 *
 * @author Javier Paniza
 */
@Entity
// Without @View
public class VehicleInsurance extends VehicleDocument {
	
	
	@Action("HidingActions.hideSave")
	public String getDescription() {
		return super.getDescription();
	}
	
	@ReferenceView("Simplest")
	public Vehicle getVehicle() {
		return super.getVehicle();
	}
	
	@Column(length=20) @Required
	private String policyNumber;

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

}
