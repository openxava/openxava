package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity @Getter @Setter	
@Tab(properties = "simulation.product.number, simulation.product.description")
public class SimulationVerification extends Identifiable {

	@ManyToOne(fetch = FetchType.LAZY)
	@ReferenceView(value = "NoDetails")
	@NoFrame
	@ReadOnly(onCreate=false) // Needed to test a case
	private Simulation simulation;
	
}
