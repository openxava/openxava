package com.yourcompany.yourapp.calculators;

import org.openxava.calculators.*;

import com.yourcompany.yourapp.model.*;

public class DefaultIssueStatusCalculator implements ICalculator {

	
	public Object calculate() throws Exception {
		IssueStatus theDefaultOne = IssueStatus.findTheDefaultOne();
		return theDefaultOne == null?null:theDefaultOne.getId();
	}
	
}
