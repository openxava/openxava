package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * tmp
 * @author Javier Paniza
 */

@MappedSuperclass
public class TaxpayerTariffBase<T extends TaxpayerBase> extends Identifiable {

	@ManyToOne(fetch = FetchType.LAZY)
	private T taxpayer;
	
	@Stereotype("MONEY")
	private BigDecimal tariff; 

	public T getTaxpayer() {
		return taxpayer;
	}
	
	public void setTaxpayer(T taxpayer) {
		this.taxpayer = taxpayer;
	}

	public BigDecimal getTariff() {
		return tariff;
	}

	public void setTariff(BigDecimal tariff) {
		this.tariff = tariff;
	}

}
