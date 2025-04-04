package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Tab(properties="id, concept.accountForConcept.account") // Don't change property names, because a bug that fail only with: concept.accountForConcept.account, just for the names 
// Don't add a view named OnlyName (that is already in Nameable) to test a case
public class SlotMachine extends Nameable {
	
	@ManyToOne
	private SlotMachineConcept concept;

	public SlotMachineConcept getConcept() {
		return concept;
	}

	public void setConcept(SlotMachineConcept concept) {
		this.concept = concept;
	}

}
