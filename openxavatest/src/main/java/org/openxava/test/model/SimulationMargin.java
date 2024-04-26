package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

import lombok.*;

/**
 * tmr SimulazioneMargine
 * 
 * @author Javier Paniza
 */

@Getter @Setter
public class SimulationMargin {
	
	@Required
	@ManyToOne(fetch = FetchType.LAZY)
	@ReferenceView(value = "NoDetails")
	@NoCreate
	@NoModify
	@NoFrame
	@OnChange(OnChangeSimulationAction.class)
    private Simulation simulation;
	
	@ElementCollection
	@ListProperties("simulationDetail.simulation.product.number, simulationDetail.product.number, simulationDetail.product.description, sellingPrice, profit")
	@EditOnly
	private Collection<SimulationMarginDetail> details;
	
	public void fillDetails() {
		// Don't change the algorithm
		Collection<SimulationMarginDetail> newDetails = new ArrayList<SimulationMarginDetail>();
		for (SimulationDetail simulationDetail : simulation.getDetails()) {
			SimulationMarginDetail simulationMarginDetail = new SimulationMarginDetail();
			simulationMarginDetail.setSimulationDetail(simulationDetail);
			newDetails.add(simulationMarginDetail);
		}
		details = newDetails;
	}
	
}
