package com.yourcompany.yourapp.actions; // In 'actions' package

import org.openxava.actions.*; // To use GoAddElementsToCollectionAction

public class GoAddOrdersToInvoiceAction
    extends GoAddElementsToCollectionAction { // Standard logic to go to
                                              // adding collection elements list
    public void execute() throws Exception {
        super.execute(); // It executes the standard logic, that shows a dialog
        int customerNumber =
            getPreviousView() // getPreviousView() is the main view (we are in a dialog)
                .getValueInt("customer.number"); // Reads the customer number
                                                 // of the current invoice from the view
        getTab().setBaseCondition( // The condition of the orders list to add
            "${customer.number} = " + customerNumber +
            " and ${delivered} = true and ${invoice} is null"
        );
    }

    public String getNextController() { // We add this method
        return "AddOrdersToInvoice"; // The controller with the available actions
    }                                // in the list of orders to add
}