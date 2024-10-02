package com.yourcompany.yourapp.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity @Getter @Setter
@View( extendsView="super.DEFAULT", // The default view
members="orders { orders }"
)
@View( name="NoCustomerNoOrders", // A view named NoCustomerNoOrders
members=                      // that does not include customer and orders
    "year, number, date;" +   // Ideal to be used from Order
    "details;" +
    "remarks"
)
@Tab(baseCondition = "${deleted} = false")
@Tab(name="Deleted", baseCondition = "${deleted} = true") // A named tab
public class Invoice extends CommercialDocument{

	//@Hidden // It will not be shown by default in views and tabs
	//@Column(columnDefinition="BOOLEAN DEFAULT FALSE") // To populate with falses instead of nulls
	//boolean deleted;
	
    @OneToMany(mappedBy="invoice")
    @CollectionView("NoCustomerNoInvoice") // This view is used to display orders
    @AddAction("Invoice.addOrders")
    private Collection<Order> orders;
    
    public static Invoice createFromOrders(Collection<Order> orders)
            throws CreateInvoiceException
        {
            Invoice invoice = null;
            for (Order order: orders) {
                if (invoice == null) { // The first order
                    order.createInvoice(); // We reuse the logic for creating an invoice
                                           // from an order
                    invoice = order.getInvoice(); // and use the created invoice
                }
                else { // For the remaining orders the invoice is already created
                    order.setInvoice(invoice); // Assign the invoice
                    order.copyDetailsToInvoice(); // A method of Order to copy the lines
                } 
            } 
            if (invoice == null) { // If there are no orders
                throw new CreateInvoiceException(
                    "orders_not_specified");
            }
            return invoice;
        }
	
}
