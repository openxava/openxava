package org.openxava.test.calculators;

import java.math.*;

import org.openxava.calculators.*;

public class TotalInvoiceCalculator implements ICalculator {
	
	private BigDecimal vat;
	private BigDecimal amountsSum;

	public Object calculate() throws Exception {		
		return getVat().add(getAmountsSum());
	}

	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}

	public BigDecimal getAmountsSum() {
		return amountsSum;
	}

	public void setAmountsSum(BigDecimal amountsSum) {
		this.amountsSum = amountsSum;
	}

}
