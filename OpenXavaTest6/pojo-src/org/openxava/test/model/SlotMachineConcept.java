package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class SlotMachineConcept extends Nameable {

	@ManyToOne
	private SlotMachineAccount accountForConcept;

	public SlotMachineAccount getAccountForConcept() {
		return accountForConcept;
	}

	public void setAccountForConcept(SlotMachineAccount accountForConcept) {
		this.accountForConcept = accountForConcept;
	}


}
