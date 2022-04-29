package org.openxava.test.calculators;

import java.math.*;

import org.openxava.calculators.*;

/**
 * @author Javier Paniza
 */

public class ConsiderableCalculator implements ICalculator {
	
	private final static BigDecimal CERO = new BigDecimal("0.00");
	
	private BigDecimal amount;
	private BigDecimal limit;
	
	public Object calculate() throws Exception {
		return new Boolean(getAmount().compareTo(getLimit()) >= 0);
	}

	public BigDecimal getAmount() {
		return amount==null?CERO:amount;
	}
	public void setAmount(BigDecimal decimal) {
		amount = decimal;
	}


	public BigDecimal getLimit() {
		return limit;
	}
	public void setLimit(BigDecimal limit) {
		this.limit = limit;
	}
	
}
