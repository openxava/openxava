package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.test.model.*;

/**
 * tmr
 * 
 * @author Javier Paniza
 */
public class OnChangeSimulationAction extends OnChangePropertyBaseAction{

	public void execute() throws Exception {
		SimulationMargin simulationMargin; 
		try {
			simulationMargin = (SimulationMargin) getView().getEntity();
		} catch (NullPointerException e) {
			return;
		}
		if(simulationMargin.getSimulation() == null) {
			return;
		}
		
		simulationMargin.fillDetails(); // This is needed to reproduce a bug afterwards
		
		getView().setValues(MapFacade.getValues(getView().getModelName(), simulationMargin, getView().getMembersNames()));
	}
	
}
