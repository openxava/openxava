package org.openxava.test.model;

import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * tmr
 * 
 * @author Javier Paniza
 */

@Entity
@Getter
@Setter
@View(extendsView = "super.DEFAULT", members = "estimatedDeliveryDays, delivered," + // Add delivered
		"invoice { invoice }")
@View(name = "NoCustomerNoInvoice", // A view named NoCustomerNoInvoice
		members = // that does not include customer and invoice.
		"year, number, date;" + // Ideal to be used from Invoice
		"details; remarks") 
public class OrderDocument extends CommercialDocument {

	@ManyToOne
	@ReferenceView("NoCustomerNoOrders") // This view is used to display invoice
	private InvoiceDocument invoice;

	@Depends("date")
	public int getEstimatedDeliveryDays() {
		if (getDate().getDayOfYear() < 15) {
			return 20 - getDate().getDayOfYear();
		}
		if (getDate().getDayOfWeek() == DayOfWeek.SUNDAY)
			return 2;
		if (getDate().getDayOfWeek() == DayOfWeek.SATURDAY)
			return 3;
		return 1;
	}

	@Column(columnDefinition = "INTEGER DEFAULT 1")
	int deliveryDays;

	// if use Validation alternative with JPA callback method
	// have to comment @PrePersist @PreUpdate
	@PrePersist
	@PreUpdate
	private void recalculateDeliveryDays() {
		setDeliveryDays(getEstimatedDeliveryDays());
	}

	@Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
	boolean delivered;

}
