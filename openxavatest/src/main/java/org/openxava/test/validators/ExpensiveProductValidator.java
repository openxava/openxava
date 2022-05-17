package org.openxava.test.validators;

import java.math.*;

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */

public class ExpensiveProductValidator implements IValidator {
	
	private int limit;
	private BigDecimal unitPrice;
	private String description;

	public void validate(Messages errors) {
		if (getDescription().indexOf("EXPENSIVE") >= 0 || getDescription().indexOf("CARO") >= 0 || getDescription().indexOf("CARA") >= 0) {
			if (getLimiteBd().compareTo(getUnitPrice()) > 0) {
				errors.add("expensive_product", getLimiteBd());
			}
		}		
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal decimal) {
		unitPrice = decimal;
	}

	public String getDescription() {
		return description==null?"":description;
	}

	public void setDescription(String string) {
		description = string;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int i) {
		limit = i;
	}
	
	private BigDecimal getLimiteBd() {
		return new BigDecimal(Integer.toString(limit));
	}

}
