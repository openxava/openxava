package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="INVOICEDETAIL")
public class InvoiceDetail2 {
	
	@ManyToOne(fetch=FetchType.LAZY)	 
	@JoinColumns({
		@JoinColumn(name="INVOICE_YEAR", referencedColumnName="YEAR"),
		@JoinColumn(name="INVOICE_NUMBER", referencedColumnName="NUMBER")
	})
	private Invoice2 invoice; // It's named invoice, instead of invoice2 in order
						// to test mappedBy in the @OneToMany. Though, in the XML version
						// this reference is invoice2 because it's automatically generated
	
	// It's calculAted in the method calculateOID
	@Id @Hidden 
	private String oid;
	
	@Column(name="QTY", length=4) @Required
	private int quantity;
	
	@Stereotype("MONEY") @Required
	private BigDecimal unitPrice;
	
	@Transient @Stereotype("FAMILY")
	private int familyList;
	
	@Transient @Stereotype("PRODUCT2")
	private long productList;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@ReferenceView("SimpleWithFamily") 
	private Product2 product;
	
	@Stereotype("MONEY") @Depends("unitPrice, quantity")
	public BigDecimal getAmount() {
		return getUnitPrice().multiply(new BigDecimal(getQuantity()));
	}
	
	@PrePersist
	private void calculateOID() {
		// Thus we can calculate an oid in a custom way

		// In the XML version (OX2) we use InvoiceDetail2 for testing injection
		// in a calculator for the oid of an aggregate from the container entity.
		// But, because OX3 does not have default-value-calculator on-create="true"
		// the implementation of InvoiceDetail and InvoiceDetail2 is the same.
		oid = invoice.getYear() + ":" + invoice.getNumber() + ":" + System.currentTimeMillis();
		recalculateInvoiceAmountsSum();
	}
	
	@PreRemove
	private void onRemove() {
		if (getInvoice().isRemoving()) return;		
		getInvoice().getDetails().remove(this);
		recalculateInvoiceAmountsSum();		
	}
	
	@PreUpdate
	private void recalculateInvoiceAmountsSum() {
		getInvoice().recalculateAmountsSum();
	}

	public String getOid() {
		return oid;
	}


	public void setOid(String oid) {
		this.oid = oid;
	}


	public Invoice2 getInvoice() {
		return invoice;
	}


	public void setInvoice(Invoice2 invoice) {
		this.invoice = invoice;
		this.invoice.getDetails().add(this);  
	}


	public int getQuantity() {
		return quantity;
	}


	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}


	public BigDecimal getUnitPrice() {
		return unitPrice;
	}


	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}



	public Product2 getProduct() {
		return product;
	}



	public void setProduct(Product2 product) {
		this.product = product;
	}

	public void setFamilyList(int familyList) {
		this.familyList = familyList;
	}

	public int getFamilyList() {
		return familyList;
	}

	public void setProductList(long productList) {
		this.productList = productList;
	}

	public long getProductList() {
		return productList;
	}

}
