package org.openxava.test.model;

import javax.persistence.*;

/**
 *  
 * @author Javier Paniza
 */

@Entity
public class IncidentActivity extends Nameable {

	@ManyToOne
	private Incident incident;

	public Incident getIncident() {
		return incident;
	}

	public void setIncident(Incident incident) {
		this.incident = incident;
	} 
	
}
