package org.openxava.test.model;

import java.io.*;

import javax.persistence.*;
import javax.persistence.Entity;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
public class Profile implements Serializable {
	
	@Id	@Column(length=5)
	private String code;
	
	@Id	
	@ManyToOne 
	@DescriptionsList(descriptionProperties="code, description", order = "${description} asc")
	private ProfileApplication application;
		
	@Column(length=50) @Required
	private String description;
	
		
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ProfileApplication getApplication() {
		return application;
	}

	public void setApplication(ProfileApplication application) {
		this.application = application;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}