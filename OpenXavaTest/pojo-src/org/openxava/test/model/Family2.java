package org.openxava.test.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.validator.constraints.*;
import org.openxava.annotations.*;

/**
 * Family2 not have oid,
 * like the typical number/description table. <p>
 * 
 * In this class we use a Hibernate Validator annotation (@Length)
 * and a Bean Validation annotation (@Max) for defining the size 
 * of the properties instead of the JPA one (@Column(length=)).<br>
 * 
 * @author Javier Paniza
 */

@Entity
@Views({ 
	@View(name="OneLine", members="number, description"),
	@View(name="Number", members="number") 
})
public class Family2 {

	@Id @Max(999)
	private int number;
	
	@Length(max=40) @Required
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
}
