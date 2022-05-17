package org.openxava.validators;



import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class MinimumSizeValidator implements IPropertyValidator {
	
	private int size;
	
	

	public void validate(Messages errors, Object value, String propertyName, String modelName) throws Exception {
		if (size == 0) return;
		if (value == null || value.toString().length() < size) {
			String units = value instanceof Number?"digits":"characters";
			errors.add("minimum_size_error", propertyName, units, new Integer(size));
		}
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
