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
 * tmp
 * @author Javier Paniza
 */

@Entity
@View(members=
	"quantity, unitPrice, amount;" +
	"product;"  +
	"remarks"
)
@EntityValidator(value=InvoiceDetailValidator6.class,
	properties= { 
		@PropertyValue(name="invoice")
	}
)
@Table(name="INVOICEDETAIL")
public class InvoiceDetail6 {
	
	@ManyToOne 
	private Invoice6 invoice;
	
	// It's calculAted in the method calculateOID
	@Id @Hidden 
	private String oid;
		
	@Column(name="QTY", length=4) // The column name does not match the property name to test sumColum for this case 
	@Required 
	private int quantity;
	
	@Stereotype("MONEY") @Required
	private BigDecimal unitPrice;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private Product product;
		
	@Stereotype("MEMO")
	private String remarks;
	
	@Stereotype("MONEY") @Depends("unitPrice, quantity")
	public BigDecimal getAmount() {
		return getUnitPrice().multiply(new BigDecimal(getQuantity()));
	}

	
	@PrePersist
	private void calculateOID() {
		oid = "invoice6:" + System.currentTimeMillis();
	}
		
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public Invoice6 getInvoice() {
		return invoice;
	}


	public void setInvoice(Invoice6 invoice) {
		this.invoice = invoice;
	}
	
}
