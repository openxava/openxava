package org.openxava.test.actions;

import org.openxava.actions.*;


/**
 * @author Javier Paniza
 */
public class OnChangeVehicleAction extends OnChangePropertyBaseAction {
	
	public void execute() throws Exception {
		String v = (String) getNewValue();
		String vehicle = v==null?"":v;
		if ("MOTORBIKE".equals(vehicle)) {
			getView().setValue("driverType", "ANY");
		}
		else if ("CAR".equals(vehicle)) {
			getView().setValue("driverType", "DRIVER");
		}
		else if ("PLANE".equals(vehicle)) {
			getView().setValue("driverType", "PILOT");
		}	
		else {
			Object value = getView().getValue("driverType"); 
			value = value == null?"":value;
			getView().setValue("driverType",  value + "X"); 							
		}
	}

}
