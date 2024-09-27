package com.yourcompany.yourapp.actions;

import java.util.*;

import javax.ejb.*;

import org.openxava.actions.*;
import org.openxava.model.*;

import com.yourcompany.invoicing.model.*;

public class CreateInvoiceFromSelectedOrdersAction
    extends TabBaseAction { // Typical for list actions. It allows you to use getTab() (1)

    public void execute() throws Exception {
        Collection<Order> orders = getSelectedOrders(); // (2)
        Invoice invoice = Invoice.createFromOrders(orders); // (3)
        addMessage("invoice_created_from_orders", invoice, orders); // (4)
        
        showDialog(); // (1)
        // From now on getView() is the dialog
        getView().setModel(invoice); // Display invoice in the dialog (2)
        getView().setKeyEditable(false); // To indicate that is an existing object (3)
        setControllers("InvoiceEdition"); // The actions of the dialog (4)
    }

    private Collection<Order> getSelectedOrders() // (5)
        throws FinderException
    {
        Collection<Order> result = new ArrayList<>();
        for (Map key: getTab().getSelectedKeys()) { // (6)
            Order order = (Order) MapFacade.findEntity("Order", key); // (7)
            result.add(order);
        }
        return result;
    }
}