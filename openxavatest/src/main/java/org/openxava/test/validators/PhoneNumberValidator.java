package org.openxava.test.validators;

import org.openxava.test.model.*;
import org.openxava.validators.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class PhoneNumberValidator implements IValidator { 

	private Country phoneCountry; 
	private String phone;
	 	
	public void validate(Messages errors) { 	
		phoneCountry.setName(phoneCountry.getName() + "X");	
	}

	public Country getPhoneCountry() {
		return phoneCountry;
	}

	public void setPhoneCountry(Country phoneCountry) {
		this.phoneCountry = phoneCountry;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	 
}