package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class TaxpayerTariff extends TaxpayerTariffBase<Taxpayer> {
	
	@ReferenceView("Name") // To test a case
	@Override
	public Taxpayer getTaxpayer() { // Taxpayer has to use inheritance too, to test a case
		return super.getTaxpayer();
	}
	
}
