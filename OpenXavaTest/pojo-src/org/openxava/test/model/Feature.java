package org.openxava.test.model;

import javax.persistence.*;

/**
 *
 * @author Javier Paniza
 */
@Embeddable
public class Feature {

	@Column(length=80)
	private String description;
	
	private int estimatedDays; // tmp

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getEstimatedDays() {
		return estimatedDays;
	}

	public void setEstimatedDays(int estimatedDays) {
		this.estimatedDays = estimatedDays;
	}
	
}
