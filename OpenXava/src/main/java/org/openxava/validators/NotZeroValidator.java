package org.openxava.validators;

import java.math.*;



import org.openxava.util.*;

/**
 * 
 * 
 * @author Javier Paniza
 */
public class NotZeroValidator implements IPropertyValidator, IWithMessage {

	private static BigDecimal ZERO_BIGDECIMAL = new BigDecimal("0");
	private static Double ZERO_DOUBLE = new Double("0");
	private static Float ZERO_FLOAT = new Float("0");

	private String message = "not_zero";
	
	public void validate(
		Messages errors,
		Object object,
		String propertyName,
		String modelName) {			
		if (object == null) {				
			errors.add(message, propertyName, modelName);
			return;
		}
		Number n = null;
		if (object instanceof Number) {
			n = (Number) object;
		}
		else if (object instanceof String) {
			try {
				n = new BigDecimal((String) object);
			}
			catch (NumberFormatException ex) {
				errors.add("numeric", propertyName, modelName);
				return;
			}
		}
		else {
			errors.add("numeric", propertyName, modelName);
			return;
		}
		if (n instanceof BigDecimal) {
			BigDecimal bd = (BigDecimal) n;
			if (bd.compareTo(ZERO_BIGDECIMAL) == 0) {
				errors.add(message, propertyName, modelName); 
			}
		}
		else if (n instanceof Double) {
			Double db = (Double) n;
			if (db.compareTo(ZERO_DOUBLE) == 0) {
				errors.add(message, propertyName, modelName); 
			}
		}
		else if (n instanceof Float) {
			Float fl = (Float) n;
			if (fl.compareTo(ZERO_FLOAT) == 0) {
				errors.add(message, propertyName, modelName); 
			}
		}
		else if (n.intValue() == 0) {
			errors.add(message, propertyName, modelName);
		}
	}
	
	public void setMessage(String message) { 
		this.message = message; 			
	}
	
}
