package org.openxava.test.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(DrivingLicenceKey.class) 
public class DrivingLicence {
	
	@Id @Column(length=2) @Stereotype("NO_FORMATING_STRING")
	private String type;
	
	@Id @Max(2)  
	private int level;
	
	@Column(length=40) @Required
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
