package org.openxava.test.model;
 
import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.calculators.*;

import lombok.*;
 
/**
 * tmr
 * 
 * @author Javier Paniza
 */
@Embeddable @Getter @Setter
public class CommercialDocumentDetail {
 
    int quantity;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    Product product;
    
    @Money
    @Depends("pricePerUnit, quantity") // pricePerUnit instead of product.number
    public BigDecimal getAmount() {
        if (pricePerUnit == null) return BigDecimal.ZERO; // pricePerUnit instead of product and product.getPrice()
        return new BigDecimal(quantity).multiply(pricePerUnit); // pricePerUnit instead of product.getPrice()
    }
 
    @DefaultValueCalculator( // tmr ¿quitar? ¿quitar también validación al grabar (en otro sitio)?
	    value=PricePerUnitCalculator.class, // This class calculates the initial value
	    properties=@PropertyValue(
	        name="productNumber", // The productNumber property of the calculator...
	        from="product.number") // ...is filled from product.number of the detail
	)
    @Money
    BigDecimal pricePerUnit; // A regular persistent property
}