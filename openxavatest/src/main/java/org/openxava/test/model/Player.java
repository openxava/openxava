package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.validators.*;

/**
 * author Jeromy Altuna
 */
@Entity
@EntityValidator(value = PlayerValidator.class, message = "{less_than_40_years_old}",
	properties = { @PropertyValue(name="birthdate") }
)
public class Player extends Nameable {
		
	private Date birthdate;

	public Date getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
}
