package org.openxava.test.validators;

import java.math.*;

import org.openxava.util.*;
import org.openxava.validators.*;

public class ForbiddenPriceValidator implements IValidator {
	
	private BigDecimal forbiddenPrice;
	private BigDecimal unitPrice;
	
	public void validate(Messages errors) throws Exception {		
		if (unitPrice == null && forbiddenPrice != null) return; 
		if (forbiddenPrice.compareTo(unitPrice) == 0) {
			errors.add("forbidden_price", forbiddenPrice);
		}
	}

	public BigDecimal getForbiddenPrice() {
		return forbiddenPrice;
	}

	public void setForbiddenPrice(BigDecimal forbiddenPrice) {
		this.forbiddenPrice = forbiddenPrice;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

}
