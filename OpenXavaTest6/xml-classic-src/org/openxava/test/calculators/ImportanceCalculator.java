package org.openxava.test.calculators;

import java.math.*;

import org.openxava.calculators.*;

/**
 * @author Javier Paniza
 */

public class ImportanceCalculator implements ICalculator {
	
	private final static BigDecimal CERO = new BigDecimal("0.00");
	
	private BigDecimal amount;
	private String importance1;
	private BigDecimal milestone1;
	private String importance2;
	private BigDecimal milestone2;
	private String importance3;
	
	public Object calculate() throws Exception {
		if (getAmount().compareTo(getMilestone1()) < 0) return importance1;
		if (getAmount().compareTo(getMilestone2()) < 0) return importance2;
		return importance3;
	}

	public BigDecimal getAmount() {
		return amount==null?CERO:amount;
	}

	public BigDecimal getMilestone1() {
		return milestone1;
	}

	public BigDecimal getMilestone2() {
		return milestone2;
	}

	public void setAmount(BigDecimal decimal) {
		amount = decimal;
	}

	public void setMilestone1(BigDecimal decimal) {
		milestone1 = decimal;
	}

	public void setMilestone2(BigDecimal decimal) {
		milestone2 = decimal;
	}



	public String getImportance1() {
		return importance1;
	}

	public String getImportance2() {
		return importance2;
	}

	public String getImportance3() {
		return importance3;
	}

	public void setImportance1(String string) {
		importance1 = string;
	}

	public void setImportance2(String string) {
		importance2 = string;
	}

	public void setImportance3(String string) {
		importance3 = string;
	}

}
