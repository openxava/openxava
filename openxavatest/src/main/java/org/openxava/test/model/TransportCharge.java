package org.openxava.test.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

/**
 * Example of reference used as key that
 * as well have a reference used as key. <p>
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(TransportChargeKey.class)
@Views({
	@View(name="WithDescriptionsList", members="delivery; amount"),
	@View(name="WithDescriptionsListShowingReferenceView", members="delivery; one{amount}; two{}"),
	@View(name="WithoutDelivery", members="amount") 
})
@Tabs({
	@Tab(properties="delivery.invoice.year, delivery.invoice.number, delivery.number, amount"),
	@Tab(name="WithDistance", properties="delivery.invoice.year, delivery.invoice.number, delivery.number, delivery.distance, amount")
})
public class TransportCharge {
	
	// tmr @Id @ManyToOne(fetch=FetchType.LAZY)
	@ManyToOne(fetch=FetchType.LAZY) // tmr
	/* This the default mapping (at least with Hibernate) tmr */  
	@JoinColumns({ 
		/* tmr
		@JoinColumn(name="DELIVERY_INVOICE_YEAR", referencedColumnName="INVOICE_YEAR"),
		@JoinColumn(name="DELIVERY_INVOICE_NUMBER", referencedColumnName="INVOICE_NUMBER"),
		@JoinColumn(name="DELIVERY_TYPE", referencedColumnName="TYPE"),
		@JoinColumn(name="DELIVERY_NUMBER", referencedColumnName="NUMBER")
		*/
		// tmr ini
		@JoinColumn(name="DELIVERY_INVOICE_YEAR", referencedColumnName="INVOICE_YEAR", insertable = false, updatable = false),
		@JoinColumn(name="DELIVERY_INVOICE_NUMBER", referencedColumnName="INVOICE_NUMBER", insertable = false, updatable = false),
		@JoinColumn(name="DELIVERY_TYPE", referencedColumnName="TYPE", insertable = false, updatable = false),
		@JoinColumn(name="DELIVERY_NUMBER", referencedColumnName="NUMBER", insertable = false, updatable = false)		
		// tmr fin
	})
	
	@ReferenceViews({
		@ReferenceView("MoreSections"),
		@ReferenceView(forViews="WithDescriptionsListShowingReferenceView", value="Simple")
	})
	@DescriptionsLists({
		@DescriptionsList(forViews="WithDescriptionsList",
			descriptionProperties="description, date", 
			condition="${invoice.year} = 2004"),
		@DescriptionsList(forViews="WithDescriptionsListShowingReferenceView",
			descriptionProperties="description, date", 
			condition="${invoice.year} = 2004",
			showReferenceView=true) 
	})
	private Delivery delivery; // Only this reference as key, to test a case
	
	// tmr ini
	@Id @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({ 
		@JoinColumn(name="DELIVERY_INVOICE_YEAR", referencedColumnName="YEAR"),
		@JoinColumn(name="DELIVERY_INVOICE_NUMBER", referencedColumnName="NUMBER"),		
	})	
	private Invoice delivery_invoice;	

	@Id @ManyToOne(fetch=FetchType.LAZY)  
	@JoinColumn(name="DELIVERY_TYPE", referencedColumnName="TYPE") 
	private DeliveryType delivery_type; 

	@Id @Column(name="DELIVERY_NUMBER")
	private int delivery_number;	
	// tmr fin
	
	@Stereotype("MONEY") @Required
	private BigDecimal amount; 
	
	public static Collection<TransportCharge> findAll() {
		Query query = XPersistence.getManager().createQuery("from TransportCharge as o"); 
 		return query.getResultList();  				
	}	

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
		// tmr ini
		this.delivery_invoice = delivery == null?null:delivery.getInvoice();
		this.delivery_number = delivery == null?null:delivery.getNumber();
		this.delivery_type = delivery == null?null:delivery.getType();
		// tmr fin
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
