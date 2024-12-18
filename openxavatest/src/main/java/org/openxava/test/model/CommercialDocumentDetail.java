package org.openxava.test.model;
 
import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;
 
/**
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
 
    @Money
    BigDecimal pricePerUnit; // A regular persistent property
}