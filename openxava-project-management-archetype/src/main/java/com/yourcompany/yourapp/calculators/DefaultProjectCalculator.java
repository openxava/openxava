package com.yourcompany.yourapp.calculators;

import org.openxava.calculators.*;

import com.yourcompany.yourapp.model.*;

public class DefaultProjectCalculator implements ICalculator {

	
	public Object calculate() throws Exception {
		Project unique = Project.findUnique();
		return unique == null?null:unique.getId();
	}
	
	

}
