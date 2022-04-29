package org.openxava.test.calculators;

import org.openxava.calculators.*;

public class ShippingAndHandlingCalculator implements ICalculator {
	
	private String stateId;
	private String stateName;	

	public Object calculate() throws Exception {
		// do nothing: Only for testing set properties from a reference to entity
		//	inside an aggregate
		return null;
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
