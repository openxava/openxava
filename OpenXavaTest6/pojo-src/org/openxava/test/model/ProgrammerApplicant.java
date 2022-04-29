package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
public class ProgrammerApplicant extends Applicant {
	
	@Column(length=60)
	private String platform;

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
}
