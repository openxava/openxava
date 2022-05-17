package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 *  
 * @author Javier Paniza
 */

@Entity
@IdClass(AppointmentTypeKey.class)
public class AppointmentType {
	
	@Id @Column(length=2)
	private String type;
	
	@Id
	private int level;
	
	@Column(length = 30) @Required
	private String description;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
