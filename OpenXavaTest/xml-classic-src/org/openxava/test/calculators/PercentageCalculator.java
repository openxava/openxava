package org.openxava.test.calculators;

import java.math.*;

import org.openxava.calculators.*;

/**
 * @author Javier Paniza
 */
public class PercentageCalculator implements ICalculator {
	
	private final static BigDecimal ZERO = new BigDecimal("0");
	private final static BigDecimal HUNDRED = new BigDecimal("100");
	
	private BigDecimal percentage;
	private BigDecimal value;
	private int scale = 2;
	private int rounding = BigDecimal.ROUND_HALF_UP;
	

	public Number getPercentage() {
		return getPercentageAsBigDecimal();
	}
	
	private BigDecimal getPercentageAsBigDecimal() {
		return percentage==null?ZERO:percentage;
	}

	public Number getValue() {
		return getValueAsBigDecimal();
	}
	
	private BigDecimal getValueAsBigDecimal() {
		return value==null?ZERO:value;
	}
	
	public void setPercentage(Number percentage) {		
		if (percentage instanceof BigDecimal) {
			this.percentage = (BigDecimal) percentage;
		}
		if (percentage == null) {
			this.percentage = ZERO;
		}else {
			this.percentage = new BigDecimal(percentage.toString());
		}		
	}

	public void setValue(Number value) {		
		if (value instanceof BigDecimal) {
			this.value = (BigDecimal) value;
		}
		else if (value == null) {
			this.value = ZERO;
		}
		else {
			this.value = new BigDecimal(value.toString());
		}
	}
	
	public Object calculate() throws Exception {		
		return getValueAsBigDecimal().multiply(getPercentageAsBigDecimal()).divide(HUNDRED, getScale(), getRounding());
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int escala) {
		this.scale = escala;
	}

	public int getRounding() {
		return rounding;
	}

	public void setRounding(int rounding) {
		this.rounding = rounding;
	}

}
