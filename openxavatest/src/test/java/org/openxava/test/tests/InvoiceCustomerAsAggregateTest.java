package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceCustomerAsAggregateTest extends ModuleTestBase {
	
	public InvoiceCustomerAsAggregateTest(String testName) {
		super(testName, "InvoiceCustomerAsAggregate");		
	}
	
	public void testCollectionsInsideReferenceAsEmbedded() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("customer.deliveryPlaces", 0);
		execute("Collection.new", "viewObject=xava_view_customer_deliveryPlaces");
		setValue("name", "JUNIT DELIVERY PLACE NAME");
		setValue("address", "JUNIT DELIVERY PLACE ADDRESS");
		execute("Collection.save");
		assertNoErrors(); 
		assertCollectionRowCount("customer.deliveryPlaces", 1);		
		checkRowCollection("customer.deliveryPlaces", 0);
		execute("Collection.removeSelected", "viewObject=xava_view_customer_deliveryPlaces");
		assertNoErrors();
		assertCollectionRowCount("customer.deliveryPlaces", 0);		
	}
									
}
