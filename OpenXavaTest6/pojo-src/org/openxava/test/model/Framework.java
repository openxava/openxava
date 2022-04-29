package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Framework extends Nameable {

	@ManyToOne
	private JavaProgrammer javaProgrammer; 
	
	@Column(length=20)
	private String language;
	
	
	public JavaProgrammer getJavaProgrammer() {
		return javaProgrammer;
	}

	public void setJavaProgrammer(JavaProgrammer javaProgrammer) {
		this.javaProgrammer = javaProgrammer;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
