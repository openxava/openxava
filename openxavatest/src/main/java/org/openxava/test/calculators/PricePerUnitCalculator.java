package org.openxava.test.calculators; // In 'calculators' package

// For using getManager()
import static org.openxava.jpa.XPersistence.getManager;

import org.openxava.calculators.*;
import org.openxava.test.model.*;

import lombok.*;
 
/**
 * tmr
 * 
 * @author Javier Paniza
 */
public class PricePerUnitCalculator implements ICalculator {
 
    @Getter @Setter
    int productNumber; // Contains the product number when calculate() is called
 
    public Object calculate() throws Exception {
        Product product = getManager() // getManager() from XPersistence
            .find(Product.class, productNumber); // Find the product
        return product.getUnitPrice(); // Returns its price
    }
 
}