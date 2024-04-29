package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

/**
 * tmr
 * 
 * @author Javier Paniza
 */

@Entity @Getter @Setter	
@Tab(properties = "simulation.product.number, simulation.product.description")
public class SimulationVerification extends Identifiable {

	@ManyToOne(fetch = FetchType.LAZY)
	@ReferenceView(value = "NoDetails")
	@NoFrame
	@ReadOnly(onCreate=false)
	private Simulation simulation;
	
}
