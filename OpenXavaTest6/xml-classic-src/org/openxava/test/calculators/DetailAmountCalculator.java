package org.openxava.test.calculators;

import java.math.*;

import org.openxava.calculators.*;

/**
 * @author Javier Paniza
 */
public class DetailAmountCalculator implements ICalculator {
	
	private static BigDecimal CERO = new BigDecimal("0.00");
	private BigDecimal unitPrice;
	private int quantity;

	public Object calculate() throws Exception {
		return getUnitPrice().multiply(new BigDecimal(Integer.toString(getQuantity())));		
	}

	public int getQuantity() {
		return quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice==null?CERO:unitPrice;
	}

	public void setQuantity(int cantidad) {
		this.quantity = cantidad;
	}

	public void setUnitPrice(BigDecimal precioUnitario) {
		this.unitPrice = precioUnitario;
	}

}
