package org.openxava.test.validators;

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * 
 * @author Javier Paniza
 */
public class MonthNameValidator implements IValidator {

	private String monthName;	
	
	public void validate(Messages errors) throws Exception {
	    if(!Is.anyEqual(monthName.toUpperCase(), "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER")) {
            throw new javax.validation.ValidationException(XavaResources.getString("month_name_invalid", monthName)); 
	    }	
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}


}