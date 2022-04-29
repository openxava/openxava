package org.openxava.test.calculators;

import java.math.*;

import org.openxava.calculators.*;

/**
 * @author Javier Paniza
 */
public class EurosToPesetasCalculator implements ICalculator {
	
	private BigDecimal euros;
	private BigDecimal rate = new BigDecimal("166.386"); 

	public EurosToPesetasCalculator() {
		super();
	}

	public Object calculate() throws Exception {
		if (euros == null) return null;
		return euros.multiply(rate).setScale(0, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getEuros() {
		return euros;
	}

	public void setEuros(BigDecimal euros) {
		this.euros = euros;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

}
