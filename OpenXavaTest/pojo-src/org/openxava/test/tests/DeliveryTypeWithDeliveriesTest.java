package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class DeliveryTypeWithDeliveriesTest extends ModuleTestBase {
	
	public DeliveryTypeWithDeliveriesTest(String testName) {
		super(testName, "DeliveryTypeWithDeliveries");		
	}
	
	public void testNewInChildCollectionWhereParentCollectionIsNotCascade() throws Exception { // tmp
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT DELIVERY TYPE");
		execute("Collection.new", "viewObject=xava_view_deliveries");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");
		setValue("number", "66");
		assertExists("date");
		execute("Collection.new", "viewObject=xava_view_details");
		assertDialogTitle("Create a new entity - Delivery detail"); // Not to test the title format but to test that we are in the correct dialog
		assertExists("number");
		assertExists("description");
		assertNotExists("date");
	}
			
}
