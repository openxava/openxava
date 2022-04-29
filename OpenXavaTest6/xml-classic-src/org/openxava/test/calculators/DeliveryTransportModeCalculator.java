package org.openxava.test.calculators;

import org.openxava.calculators.*;

/**
 * @author Javier Paniza
 */
public class DeliveryTransportModeCalculator implements ICalculator {
	
	private String vehicle; 

	public Object calculate() throws Exception {
		if ("PLANE".equalsIgnoreCase(vehicle)) return "AIR";
		if ("MOTORBIKE".equalsIgnoreCase(vehicle)) return "STREET/ROAD";		
		if ("CAR".equalsIgnoreCase(vehicle)) return "HIGHWAY";
		return "";		
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String string) {
		vehicle = string;
	}

}
