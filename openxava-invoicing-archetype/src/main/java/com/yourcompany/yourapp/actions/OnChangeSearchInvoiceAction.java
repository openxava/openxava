package com.yourcompany.yourapp.actions; // In 'actions' package

import java.util.*;

import org.openxava.actions.*; // To use OnChangeSearchAction
import org.openxava.model.*;
import org.openxava.view.*;

import com.yourcompany.yourapp.model.*;

public class OnChangeSearchInvoiceAction 
    extends OnChangeSearchAction { // Standard logic for searching a reference when
                                   // the key values change in the user interface 
    public void execute() throws Exception {
        super.execute(); // It executes the standard logic 
        Map keyValues = getView()// getView() here is the reference view, not the main one 
            .getKeyValuesWithValue();
        if (keyValues.isEmpty()) return; // If key is empty no additional logic is executed
        Invoice invoice = (Invoice) // We search the Invoice entity from the typed key 
            MapFacade.findEntity(getView().getModelName(), keyValues);
        View customerView = getView().getRoot().getSubview("customer"); 
        int customerNumber = customerView.getValueInt("number");
        if (customerNumber == 0) { // If there is no customer we fill it
            customerView.setValue("number", invoice.getCustomer().getNumber());
            customerView.refresh();
        } 
        else { // If there is already customer we verify that he matches the invoice customer 
            if (customerNumber != invoice.getCustomer().getNumber()) {
                addError("invoice_customer_not_match", 
                    invoice.getCustomer().getNumber(), invoice, customerNumber);
                getView().clear();
            }
        }
    }
}	