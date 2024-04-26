package org.openxava.test.model;

import java.io.*;

import javax.persistence.*;

import lombok.*;

/**
 * tmr
 * 
 * @author Javier Paniza
 */

@Data
public class SimulationDetailKey implements Serializable {
	
	@Id
	@ManyToOne
	Simulation simulation;
	
	@Id
	@ManyToOne
	Product product;

}
