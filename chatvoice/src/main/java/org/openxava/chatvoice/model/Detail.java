package org.openxava.chatvoice.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import org.openxava.chatvoice.calculators.*;

import lombok.*;

// RENAME THIS CLASS AS InvoiceDetail, PurchaseOrderDetail, WorkOrderDetail, DeliveryDetail, AcountTransaction, ShipmentDetail, etc.

// YOU CAN RENAME THE MEMBERS BELOW AT YOUR CONVENIENCE, 
// FOR EXAMPLE unitPrice BY hourPrice,
// BUT CHANGE ALL REFERENCES IN ALL CODE USING SEARCH AND REPLACE FOR THE PROJECT. 
// DON'T USE REFACTOR > RENAME FOR MEMBERS BECAUSE IT DOESN'T CHANGE THE ANNOTATIONS CONTENT.

@Embeddable @Getter @Setter
public class Detail {
		
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	Product product;

	@Required 
	@DefaultValueCalculator(  
		value=UnitPriceCalculator.class,
		properties=@PropertyValue(
			name="number",
			from="product.number")
	)
	BigDecimal unitPrice;
		
	@Required
	int quantity;	

	@ReadOnly @Money
	@Calculation("unitPrice * quantity") 
	BigDecimal amount;
	
}
