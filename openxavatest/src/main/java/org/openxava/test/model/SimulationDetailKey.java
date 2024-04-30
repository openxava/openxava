package org.openxava.test.model;

import java.io.*;

import javax.persistence.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

@Data
public class SimulationDetailKey implements Serializable {
	
	@Id
	@ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY needed to test a case 
	Simulation simulation;
	
	@Id
	@ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY needed to test a case
	Product product;

}
