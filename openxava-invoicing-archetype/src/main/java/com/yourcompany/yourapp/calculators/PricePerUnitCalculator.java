package com.yourcompany.yourapp.calculators; // In 'calculators' package

// For using getManager()
import static org.openxava.jpa.XPersistence.getManager;

import org.openxava.calculators.*;

import com.yourcompany.yourapp.model.*;

import lombok.*;
 
public class PricePerUnitCalculator implements ICalculator {
 
    @Getter @Setter
    int productNumber; // Contains the product number when calculate() is called
 
    public Object calculate() throws Exception {
        Product product = getManager() // getManager() from XPersistence
            .find(Product.class, productNumber); // Find the product
        return product.getPrice(); // Returns its price
    }
 
}