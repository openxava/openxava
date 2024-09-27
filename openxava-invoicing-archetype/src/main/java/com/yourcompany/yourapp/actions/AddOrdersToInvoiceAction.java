package com.yourcompany.yourapp.actions; // In 'actions' package

import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.openxava.actions.*; // To use AddElementsToCollectionAction
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

import com.yourcompany.invoicing.model.*;

public class AddOrdersToInvoiceAction
    extends AddElementsToCollectionAction { // Standard logic for adding
                                            // collection elements
    public void execute() throws Exception {
        super.execute(); // We use the standard logic "as is"
        getView().refresh(); // To display fresh data, including recalculated
    }                        // amounts, which depend on detail lines

    protected void associateEntity(Map keyValues) // The method called to associate
        throws ValidationException, // each entity to the main one, in this case to
            XavaException, ObjectNotFoundException,// associate each order to the invoice
            FinderException, RemoteException
    {
        super.associateEntity(keyValues); // It executes the standard logic (1)
        Order order = (Order) MapFacade.findEntity("Order", keyValues); // (2)
        order.copyDetailsToInvoice(); // Delegates the main work to the entity (3)
    }
}