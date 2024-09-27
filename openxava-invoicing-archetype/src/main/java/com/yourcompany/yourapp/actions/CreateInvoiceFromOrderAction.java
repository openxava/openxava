package com.yourcompany.yourapp.actions; // In 'actions' package

import org.openxava.actions.*;

import com.yourcompany.invoicing.model.*;

public class CreateInvoiceFromOrderAction extends ViewBaseAction { // To use getView()

	public void execute() throws Exception {
		if (getView().getValue("oid") == null) {
			// If oid is null the current order is not saved yet (1)
			addError("impossible_create_invoice_order_not_exist");
			return;
		}
		Order order = (Order) getView().getEntity(); // Order entity displayed in the view (1)
		order.createInvoice(); // The real work is delegated to the entity (2)
		getView().refresh(); // In order to see the created invoice in 'Invoice' tab (3)
		addMessage("invoice_created_from_order", // Confirmation message (4)
				order.getInvoice());
		removeActions("Order.createInvoice");
	}
}