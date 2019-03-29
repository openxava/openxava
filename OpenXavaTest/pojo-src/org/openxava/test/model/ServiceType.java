package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class ServiceType {
	
	@Id @Column(length=3)
	private int number;
	
	@Column(length=30) @Required
	private String description;
	
	@Required @Stereotype("FAMILY")
	private int family;
	
	@Required @Stereotype("SUBFAMILY")
	private int subfamily;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFamily() {
		return family;
	}

	public void setFamily(int family) {
		this.family = family;
	}

	public int getSubfamily() {
		return subfamily;
	}

	public void setSubfamily(int subfamily) {
		this.subfamily = subfamily;
	}

}
