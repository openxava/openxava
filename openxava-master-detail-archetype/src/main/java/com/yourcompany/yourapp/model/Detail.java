package com.yourcompany.yourapp.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.yourcompany.yourapp.calculators.*;

import lombok.*;

// RENAME THIS CLASS AS InvoiceDetail, PurchaseOrderDetail, WorkOrderDetail, DeliveryDetail, AcountTransaction, ShipmentDetail, etc.

// YOU CAN RENAME THE MEMBERS BELOW AT YOUR CONVENIENCE, 
// FOR EXAMPLE unitPrice BY hourPrice,
// BUT CHANGE ALL REFERENCES IN ALL CODE USING SEARCH AND REPLACE FOR THE PROJECT. 
// DON'T USE REFACTOR > RENAME FOR MEMBERS BECAUSE IT DOESN'T CHANGE THE ANNOTATIONS CONTENT.

@Embeddable @Getter @Setter
public class Detail {
		
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	Item item;

	@Required 
	@DefaultValueCalculator(  
		value=UnitPriceCalculator.class,
		properties=@PropertyValue(
			name="number",
			from="item.number")
	)
	BigDecimal unitPrice;
		
	@Required
	int quantity;	

	@ReadOnly @Money
	@Calculation("unitPrice * quantity") 
	BigDecimal amount;
	
}
