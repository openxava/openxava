package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * Model class for the @Collapsed annotation tests.
 * 
 * @author Paco Valsera
 */

public class Teacher {
	
	@Required
	private String name;
	
	@ManyToOne
	@Collapsed
	private School school;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}		
	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}
}
