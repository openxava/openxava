package org.openxava.test.model;

import java.util.*;
import org.openxava.annotations.*;

/**
 * Model class for the @Collapsed annotation tests.
 * 
 * @author Paco Valsera
 *  
 */
public class School {
	
	@Required
	private String name;
		
	@Collapsed
	private Collection<Teacher> teachers;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Teacher> getTeachers() {
		return teachers;
	}

	public void setTeachers(Collection<Teacher> teachers) {
		this.teachers = teachers;
	}
}
