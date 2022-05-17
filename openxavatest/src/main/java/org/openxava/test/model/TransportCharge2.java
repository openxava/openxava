package org.openxava.test.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

/**
 * Example of a reference used as key that
 * as well have a reference used as key. <p>
 * 
 * Also the reference is overlapped with a key property.
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(TransportCharge2Key.class)
@Tabs({
	@Tab(properties="delivery.invoice.year, delivery.invoice.number, delivery.number, amount"),
	@Tab(name="WithDistance", properties="delivery.invoice.year, delivery.invoice.number, delivery.number, delivery.distance, amount")
})
public class TransportCharge2 {
	
	@Id @Column(length=4) 
	private int year;
	
	@ManyToOne(fetch=FetchType.LAZY) 	 
	@JoinColumns({ 
		@JoinColumn(name="YEAR", referencedColumnName="INVOICE_YEAR", insertable=false, updatable=false),
		@JoinColumn(name="DELIVERY_INVOICE_NUMBER", referencedColumnName="INVOICE_NUMBER", insertable=false, updatable=false),
		@JoinColumn(name="DELIVERY_TYPE", referencedColumnName="TYPE", insertable=false, updatable=false),
		@JoinColumn(name="DELIVERY_NUMBER", referencedColumnName="NUMBER", insertable=false, updatable=false)
	})	
	private Delivery delivery;
	@Id @Hidden
	private Integer delivery_invoice_number;
	@Id @Hidden
	private Integer delivery_type;
	@Id @Hidden
	private Integer delivery_number;
		
	@Stereotype("MONEY") @Required
	private BigDecimal amount; 
	
	public static Collection<TransportCharge2> findAll() {
		Query query = XPersistence.getManager().createQuery("from TransportCharge2 as o"); 
 		return query.getResultList();  				
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}	

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
		this.delivery_invoice_number = delivery == null?null:delivery.getInvoice().getNumber();
		this.delivery_type = delivery == null?null:delivery.getType().getNumber();
		this.delivery_number = delivery == null?null:delivery.getNumber();
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getDelivery_invoice_number() { 
		return delivery_invoice_number;
	}

	public void setDelivery_invoice_number(Integer delivery_invoice_number) { 
		this.delivery_invoice_number = delivery_invoice_number;
	}

	public Integer getDelivery_number() { 
		return delivery_number;
	}

	public void setDelivery_number(Integer delivery_number) { 
		this.delivery_number = delivery_number;
	}

	public Integer getDelivery_type() { 
		return delivery_type;
	}

	public void setDelivery_type(Integer delivery_type) { 
		this.delivery_type = delivery_type;
	}

}
