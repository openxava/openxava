package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@DiscriminatorValue("DOC")
public class Doctor extends Human {
	
	@Column(length=80)
	private String currentHospital;

	public String getCurrentHospital() {
		return currentHospital;
	}

	public void setCurrentHospital(String currentHospistal) {
		this.currentHospital = currentHospistal;
	}

}
