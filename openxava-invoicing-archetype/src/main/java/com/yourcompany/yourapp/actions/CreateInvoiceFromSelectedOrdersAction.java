package com.yourcompany.yourapp.actions;

import java.util.*;

import javax.ejb.*;

import org.openxava.actions.*;
import org.openxava.model.*;

import com.yourcompany.yourapp.model.*;

public class CreateInvoiceFromSelectedOrdersAction
    extends TabBaseAction { // Typical for list actions. It allows you to use getTab() 

    public void execute() throws Exception {
        Collection<Order> orders = getSelectedOrders(); 
        Invoice invoice = Invoice.createFromOrders(orders); 
        addMessage("invoice_created_from_orders", invoice, orders); 
        
        showDialog(); 
        // From now on getView() is the dialog
        getView().setModel(invoice); // Display invoice in the dialog 
        getView().setKeyEditable(false); // To indicate that is an existing object 
        setControllers("InvoiceEdition"); // The actions of the dialog 
    }

    private Collection<Order> getSelectedOrders() 
        throws FinderException
    {
        Collection<Order> result = new ArrayList<>();
        for (Map key: getTab().getSelectedKeys()) { 
            Order order = (Order) MapFacade.findEntity("Order", key); 
            result.add(order);
        }
        return result;
    }
}
