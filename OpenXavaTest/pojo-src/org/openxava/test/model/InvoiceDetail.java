package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Parameter;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.test.validators.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Views({
	@View(members=
		"serviceType;" +
		"quantity, unitPrice, amount;" +
		"product;"  +
		"deliveryDate, soldBy;" +	
		"remarks"
	),
	@View(name="AllMembersInSections", members=
		"overview { " +
			"serviceType;" +
			"product;" +
			"deliveryDate, soldBy;" +	
			"remarks" + 				
		"}" +
		"amounts { " +
			"quantity, unitPrice, amount;" +
		"}"
	)
})
@EntityValidator(value=InvoiceDetailValidator.class,
	properties= { 
		@PropertyValue(name="invoice"), 
		@PropertyValue(name="oid"), 
		@PropertyValue(name="product"),
		@PropertyValue(name="unitPrice"),
		@PropertyValue(name="amount")
	}
)
public class InvoiceDetail {
	
	@ManyToOne // Lazy fetching produces a fails on removing a detail from invoice
	/*
	 This mapping is the assumed one	
	  @JoinColumns({
		@JoinColumn(name="INVOICE_YEAR", referencedColumnName="YEAR"),
		@JoinColumn(name="INVOICE_NUMBER", referencedColumnName="NUMBER")
	})
	*/
	private Invoice invoice;
	
	// It's calculAted in the method calculateOID
	@Id @Hidden 
	private String oid;
	
	@org.hibernate.annotations.Type(type="org.openxava.types.Base1EnumType", 
		parameters={			
			@Parameter(name="enumType", value="org.openxava.test.model.InvoiceDetail$ServiceType")
		}
	)
	private ServiceType serviceType;
	public enum ServiceType { SPECIAL, URGENT }
	
	@Column(name="QTY", length=4) // The column name does not match the property name to test sumColum for this case 
	@Required 
	private int quantity;
	
	@Stereotype("MONEY") @Required
	private BigDecimal unitPrice;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private Product product;
	
	@Type(type="org.openxava.types.Date3Type") 
	@Columns(columns = { 
		@Column(name="YEARDELIVERY"), 
		@Column(name="MONTHDELIVERY"), 
		@Column(name="DAYDELIVERY") 
	})	
	@DefaultValueCalculator(CurrentDateCalculator.class)	
	private java.util.Date deliveryDate;
	
	@ManyToOne(fetch=FetchType.LAZY) 	
	@DescriptionsList
	private Seller soldBy;
	
	@Stereotype("MEMO")
	private String remarks;
	
	@Stereotype("MONEY") @Depends("unitPrice, quantity")
	public BigDecimal getAmount() {
		return getUnitPrice().multiply(new BigDecimal(getQuantity()));
	}

	public boolean isFree() {
		return getAmount().compareTo(new BigDecimal("0")) <= 0;
	}
	
	@PrePersist
	private void calculateOID() {
		// Thus we can calculate an oid in a custom way
		
		// In EJB2 and Hibernate version we use a counter as third element (instead of
		// a currentTimeMillis), but in EJB3 version of OpenXava this is not supported,
		// because it's better to use the standard oid generation of JPA, and rarely
		// to receive a sequential counter from UI would be useful.
		
		// That is the technique of org.openxava.calculators.IAggregateOidCalculator
		// is deprecated in OX3 
		oid = invoice.getYear() + ":" + invoice.getNumber() + ":" + System.currentTimeMillis();
	}
	
	//@PostRemove // Literal translation of postremove-calculator, though it does not work fine with READ COMMITED 
	@PreRemove  // Works fine with READ COMMITED, though it's not a literal translation of postremove-calculator XML component counterpart 
	private void postRemove() {
		invoice.setComment(invoice.getComment() + "DETAIL DELETED");
	}
	
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public ServiceType getServiceType() {
		return serviceType;
	}
	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice==null?BigDecimal.ZERO:unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public java.util.Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(java.util.Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Seller getSoldBy() {
		// In this way because the column for 'soldBy' does not admit null
		try {
			if (soldBy != null) soldBy.toString(); // to force load
			return soldBy;
		}
		catch (EntityNotFoundException ex) {			
			return null;  
		}
	}

	public void setSoldBy(Seller soldBy) {
		this.soldBy = soldBy;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
}
