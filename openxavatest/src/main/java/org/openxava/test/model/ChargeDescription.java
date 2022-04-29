package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class ChargeDescription extends Identifiable {
	
	@Required @Column(length = 50)
	private String description;
	
	@ReadOnly
	@ReferenceView("WithoutDelivery")
	@ManyToOne(fetch = FetchType.LAZY)
	private TransportCharge transportCharge;

	public TransportCharge getTransportCharge() {
		return transportCharge;
	}

	public void setTransportCharge(TransportCharge transportCharge) {
		this.transportCharge = transportCharge;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
