package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * tmp
 * @author Javier Paniza
 */

@Entity
public class TaxpayerTariff extends TaxpayerTariffBase<Taxpayer> {
	
	@ReferenceView("Name")
	@Override
	public Taxpayer getTaxpayer() {
		return super.getTaxpayer();
	}
	
}
