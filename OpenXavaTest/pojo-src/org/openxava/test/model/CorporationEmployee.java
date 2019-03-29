package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Laurent Wibaux
 */

@Entity
public class CorporationEmployee extends Identifiable {

	@ManyToOne
	@ReferenceView("Simple") @NoFrame
	private Corporation corporation;
	
	@Required
	private String firstName;
	
	@Required
	private String lastName;
	
	@Required
	private String email;

	private int salary;
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public int getSalary() {
		return salary;
	}

	public void setCorporation(Corporation corporation) {
		this.corporation = corporation;
	}

	public Corporation getCorporation() {
		return corporation;
	}
	
}
