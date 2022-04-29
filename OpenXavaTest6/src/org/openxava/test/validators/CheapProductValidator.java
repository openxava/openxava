package org.openxava.test.validators;

import java.math.*;

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */

public class CheapProductValidator implements IValidator {
	
	private int limit;
	private BigDecimal unitPrice;
	private String description;

	public void validate(Messages errors) {
		if (getDescription().indexOf("CHEAP") >= 0 || getDescription().indexOf("BARATO") >= 0 || getDescription().indexOf("BARATA") >= 0) {
			if (getLimitBd().compareTo(getUnitPrice()) < 0) {
				errors.add("cheap_product", getLimitBd());
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
	
	private BigDecimal getLimitBd() {
		return new BigDecimal(Integer.toString(limit));
	}

}
