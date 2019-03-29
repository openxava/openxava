package org.openxava.test.calculators;

import java.math.*;

import org.openxava.calculators.*;
import org.openxava.test.model.*;

/**
 * @author Javier Paniza
 */

public class ExportPriceCalculator implements ICalculator {
	
	private BigDecimal euros;
	private String country;
	private BigDecimal tariff;

	public Object calculate() throws Exception {
		if ("España".equals(country) || "Guatemala".equals(country)) {
			return euros.add(tariff);   
		}
		else {
			throw new PriceException("Country not register");
		}		
	}

	public BigDecimal getEuros() {
		return euros;
	}

	public void setEuros(BigDecimal decimal) {
		euros = decimal;
	}

	public BigDecimal getTariff() {
		return tariff;
	}

	public String getCountry() {
		return country;
	}

	public void setTariff(BigDecimal decimal) {
		tariff = decimal;
	}

	public void setCountry(String string) {
		country = string;
	}

}
