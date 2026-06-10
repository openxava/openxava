package org.openxava.test.model;

import jakarta.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
public class ProgrammingSkill extends Skill {

	@Column(length=20)
	private String language;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
}
