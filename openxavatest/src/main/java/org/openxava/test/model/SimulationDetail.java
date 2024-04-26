package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * tmr PadreFiglio 
 * 
 * @author Javier Paniza
 */
@Entity @Getter @Setter
@IdClass(SimulationDetailKey.class)
@View(members="simulation;"
 	   + "product;"
 	   + "weightPercentage"
)

@Tab(properties = "simulation.product.number, simulation.product.description, product.number, product.description, weightPercentage")
public class SimulationDetail {
	
	@Id
	@ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY needed to test a case
	@ReferenceView(value = "NoDetails")
	@NoFrame
	private Simulation simulation;
	
	@Id
	@ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY needed to test a case
	@NoCreate
	@NoModify
	@ReferenceView("Simple")
	private Product product;
	
	@ReadOnly
	@Column(precision = 5, scale = 2)
	private BigDecimal weightPercentage;
	
	public BigDecimal calculateProfit(BigDecimal sellingPrice) {
		if(sellingPrice == null) {
			return BigDecimal.ZERO; 
		} else {
			return new BigDecimal(2).multiply(sellingPrice);
		}		
	}
	
}