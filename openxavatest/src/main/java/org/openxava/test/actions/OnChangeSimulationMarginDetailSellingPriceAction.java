package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.test.model.*;

/**
 * tmr
 * @author Javier Paniza
 */
public class OnChangeSimulationMarginDetailSellingPriceAction extends OnChangePropertyBaseAction{

	public void execute() throws Exception {
		SimulationMargin simulationMargin;
		try {
			simulationMargin = (SimulationMargin) getView().getParent().getEntity();
		} catch (NullPointerException e) {
			return;
		}
		if(simulationMargin.getSimulation()== null) {	
			return;
		}
		
		Map values = MapFacade.getValues(getView().getParent().getModelName(), simulationMargin, getView().getParent().getMembersNames());
		
		getView().getParent().setValues(values); // Here was the error
		
		addMessage("simulation_data_reloaded"); 
		
	}
	
}
