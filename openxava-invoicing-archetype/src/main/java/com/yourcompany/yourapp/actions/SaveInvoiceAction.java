package com.yourcompany.yourapp.actions;

import org.openxava.actions.*;

public class SaveInvoiceAction
    extends SaveAction { // Standard OpenXava action to save the view content
	 
    public void execute() throws Exception {
        super.execute(); // The standard saving logic 
        closeDialog(); 
    }
}