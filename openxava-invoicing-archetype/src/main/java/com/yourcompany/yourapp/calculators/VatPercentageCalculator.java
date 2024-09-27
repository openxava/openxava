package com.yourcompany.yourapp.calculators; // In 'calculators' package

import org.openxava.calculators.*; // To use ICalculator

import com.yourcompany.invoicing.util.*; // To use InvoicingPreferences
 
public class VatPercentageCalculator implements ICalculator {
 
    public Object calculate() throws Exception {
        return InvoicingPreferences.getDefaultVatPercentage();
    }
}