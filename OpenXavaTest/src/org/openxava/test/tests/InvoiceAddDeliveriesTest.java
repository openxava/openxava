package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceAddDeliveriesTest extends ModuleTestBase {
	
	public InvoiceAddDeliveriesTest(String testName) {
		super(testName, "InvoiceAddDeliveries");		
	}
	
	public void testCustomAddActionInCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		assertNoAction("Collection.add");
		execute("Invoice.addDelivery", "viewObject=xava_view_deliveries");		
		assertMessage("Choose a delivery to add to the invoice");
	}
	
}
