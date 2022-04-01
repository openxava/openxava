package org.openxava.validators;

import org.openxava.util.*;
import org.openxava.validators.IPropertyValidator;

/**
 * 
 * @author Janesh Kodikara
 */

public class PhoneNumberValidator implements IPropertyValidator {

    private long minSize = 8;
    private Long phoneNumber;

    public void validate(Messages errors, Object value, String propertyName,
    	String modelName) throws Exception {

        if (value == null || value.toString().length() ==0 ) return;

        //Check if input is a number
        try {
          phoneNumber = new  Long(value.toString());
        } catch(NumberFormatException ex){
           errors.add("phone_valid_number_error", propertyName);
           return;

        }

        //Check if input length is at least greater than specified minimum length
        if ( phoneNumber.toString().length() < minSize) {
            errors.add("phone_minimum_size_error", propertyName, "digits", new Long(minSize));
            return;
        }


    }

    public long getMinSize() {
        return minSize;
    }

    public void setMinSize(long minSize) {
        this.minSize= minSize;
    }

}