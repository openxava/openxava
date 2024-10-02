package com.yourcompany.yourapp.actions; // In 'actions' package

import org.openxava.actions.*; // To use ReferenceSearchAction

public class SearchInvoiceFromOrderAction
    extends ReferenceSearchAction { // Standard logic for searching a reference

    public void execute() throws Exception {
        int customerNumber =
            getView().getValueInt("customer.number"); // Reads from the view the
                                         // customer number of the current order
        super.execute(); // It executes the standard logic that shows a dialog
        if (customerNumber > 0) { // If there is customer we use it to filter
            getTab().setBaseCondition("${customer.number} = " + customerNumber);
        }
    }
}