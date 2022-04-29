package org.openxava.test.calculators;

import org.openxava.calculators.*;

public class IsLocalCustomerCalculator implements ICalculator {
	
	private String stateId;
	private String stateName;

	public Object calculate() throws Exception {
		// do nothing: Only for testing set properties from a reference to entity
		//	inside an aggregate		
		return Boolean.FALSE;
	}

	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

}
