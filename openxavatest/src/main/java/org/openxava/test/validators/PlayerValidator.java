package org.openxava.test.validators;

import java.util.*;

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Jeromy Altuna
 */
public class PlayerValidator implements IValidator, IWithMessage {
	
	private static final long serialVersionUID = 1079317203011541680L;
	
	private String message;
	private Date birthdate;
	
	@Override
	public void validate(Messages errors) throws Exception {
		if (Dates.dateDistance(new Date(), birthdate).years < 40) return;
		errors.add(message);		
	}
	
	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	public Date getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
}
