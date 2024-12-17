package org.openxava.test.model;

import java.time.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.util.*;

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

	// Validation alternative with Bean Validation, if use this, you have to change
	// in i18n for
	// order_must_be_delivered=Order {year}/{number} must be delivered in order to
	// be added to an Invoice
	@AssertTrue( // Before saving it asserts if this method returns true, if not it throws an
					// exception
			message = "order_must_be_delivered" // Error message in case false
	)
	private boolean isDeliveredToBeInInvoice() {
		return invoice == null || isDelivered(); // The validation logic
	}

	// Validation alternative with JPA callback method
	@PreRemove
	private void validateOnRemove() { // Now this method is not executed automatically
		if (invoice != null) { // since a real deleletion is not done
			throw new javax.validation.ValidationException(XavaResources.getString("cannot_delete_order_with_invoice"));
		}
	}

    // This method must return true for this order to be valid
    @AssertTrue(message="customer_order_invoice_must_match") 
    private boolean isInvoiceCustomerMatches() {
    	return invoice == null || // invoice is optional
    		invoice.getCustomer().getNumber() == getCustomer().getNumber();
    }
    
}
