package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceDeliveriesTest extends ModuleTestBase {
	
	public InvoiceDeliveriesTest(String testName) {
		super(testName, "InvoiceDeliveries");		
	}
	
	public void testOverwriteViewActionInCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("Invoice.viewDelivery", "row=0,viewObject=xava_view_deliveries");		
		assertMessage("Delivery displayed"); 
	}
	
	public void testLabelInCollection() throws Exception {
		execute("CRUD.new");
		assertLabelInCollection("deliveries", 0, "This is my label");
	}
}
