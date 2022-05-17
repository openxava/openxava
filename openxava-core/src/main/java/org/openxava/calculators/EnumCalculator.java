package org.openxava.calculators;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class EnumCalculator implements ICalculator {
	
	private static Log log = LogFactory.getLog(EnumCalculator.class);
	
	private String enumType;
	private String value;

	public Object calculate() throws Exception {
		if (Is.emptyString(enumType)) {
			throw new XavaException("value_in_property_of_calculator_required", "enumType", getClass().getName()); 
		}
		if (Is.emptyString(value)) return null;		
		try {
			return Enum.valueOf((Class<Enum>) Class.forName(enumType), value);
		} 
		catch (Exception ex) {
			String message = XavaResources.getString("value_of_enum_error", enumType, value); 
			log.error(message, ex);
			throw new XavaException(message);
		}				
	}

	public String getEnumType() {
		return enumType;
	}

	public void setEnumType(String enumType) {		
		this.enumType = enumType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
