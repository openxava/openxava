package com.yourcompany.yourapp.model;

import java.time.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.apache.commons.beanutils.*;
import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.yourcompany.yourapp.actions.*;

import lombok.*;

@Entity
@Getter
@Setter
@View(extendsView = "super.DEFAULT", members = "estimatedDeliveryDays, delivered," + // Add delivered
		"invoice { invoice }")
@View(name = "NoCustomerNoInvoice", // A view named NoCustomerNoInvoice
		members = // that does not include customer and invoice.
		"year, number, date;" + // Ideal to be used from Invoice
				"details;" + "remarks")
@Tab(baseCondition = "${deleted} = false")
@Tab(name = "Deleted", baseCondition = "${deleted} = true") // A named tab
public class Order extends CommercialDocument {

	@ManyToOne
	@ReferenceView("NoCustomerNoOrders") // This view is used to display invoice
	@OnChange(ShowHideCreateInvoiceAction.class) // Add this
	@SearchAction("Order.searchInvoice") // To define our own action to search invoices
	@OnChangeSearch(OnChangeSearchInvoiceAction.class)
	private Invoice invoice;

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
	@OnChange(ShowHideCreateInvoiceAction.class) // Add this
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

	public void setDeleted(boolean deleted) {
		if (deleted)
			validateOnRemove(); // We call the validation explicitly
		super.setDeleted(deleted);
	}

	public void createInvoice() throws CreateInvoiceException // An application exception 
	{
		if (this.invoice != null) { // If an invoice is already present we cannot create one
			throw new CreateInvoiceException("order_already_has_invoice"); // Allows an i18n id as argument
		}
		if (!isDelivered()) { // If the order is not delivered we cannot create the invoice
			throw new CreateInvoiceException("order_is_not_delivered");
		}
		try {
			Invoice invoice = new Invoice();
			BeanUtils.copyProperties(invoice, this);
			invoice.setOid(null);
			invoice.setDate(LocalDate.now());
			invoice.setDetails(new ArrayList<>(getDetails()));
			XPersistence.getManager().persist(invoice);
			this.invoice = invoice;
		} catch (Exception ex) { // Any unexpected exception 
			throw new SystemException( // A runtime exception is thrown 
					"impossible_create_invoice", ex);
		}
	}

	public void copyDetailsToInvoice() {
		invoice.getDetails().addAll(getDetails()); // Copies the lines
		invoice.setVat(invoice.getVat().add(getVat())); // Accumulates the vat
		invoice.setTotalAmount( // and the total amount
				invoice.getTotalAmount().add(getTotalAmount()));
	}

    // This method must return true for this order to be valid
    @AssertTrue(message="customer_order_invoice_must_match") 
    private boolean isInvoiceCustomerMatches() {
    	return invoice == null || // invoice is optional
    		invoice.getCustomer().getNumber() == getCustomer().getNumber();
    }
    
}
