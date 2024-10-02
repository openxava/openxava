package com.yourcompany.yourapp.model; // In model package

import org.openxava.util.*;

public class CreateInvoiceException extends Exception { // Not RuntimeException

    public CreateInvoiceException(String message) {
        // The XavaResources is to translate the message from the i18n entry id
        super(XavaResources.getString(message)); 
    }
	
}