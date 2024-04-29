package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

@Getter @Setter 
public class SimulationMarginDetail {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@ReadOnly
	SimulationDetail simulationDetail;
	
	@Column(precision = 7, scale = 2)
	@OnChange(OnChangeSimulationMarginDetailSellingPriceAction.class)
	private BigDecimal sellingPrice;

	@Column(precision = 7, scale = 2)
	@Depends("sellingPrice")
	public BigDecimal getProfit() {	
		return simulationDetail.calculateProfit(sellingPrice);
	}
	
}
