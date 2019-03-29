package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Jeromy Altuna
 */
@Embeddable
public class ServiceExpense2 {
	
	@ManyToOne(fetch=FetchType.LAZY)
	private ServiceInvoice invoice;
	
	@Required
	private Status status;
	private enum Status { PAID, PENDING, REJECTED }
	
	@ManyToOne(fetch=FetchType.LAZY) @DescriptionsList
	private Receptionist receptionist; 
	
	public ServiceInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(ServiceInvoice invoice) {
		this.invoice = invoice;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Receptionist getReceptionist() {
		return receptionist;
	}

	public void setReceptionist(Receptionist receptionist) {
		this.receptionist = receptionist;
	}
}
