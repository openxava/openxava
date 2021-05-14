package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * tmp
 * @author Javier Paniza
 */

public class OrderIssueOrderAsEmbeddedTest extends ModuleTestBase {
	
	public OrderIssueOrderAsEmbeddedTest(String testName) {
		super(testName, "OrderIssueOrderAsEmbedded");		
	}
	
	public void testCreateCollectionElementInAnEmbeddedReference() throws Exception {
		// It failed with OrderIssue, Order and OrderDetail, but not in other cases like Invoice with Customer and DeliverPlace,
		// perhaps because OrderIssue, Order and OrderDetail are Identifiable
		execute("CRUD.new");
		setValue("date", "5/14/2021");
		setValue("description", "JUNIT ORDER ISSUE");
		setValue("order.customer.number", "1");
		assertValue("order.customer.name", "Javi");
		setValue("order.remarks", "JUNIT ORDER CREATED FROM ORDER ISSUE");
		assertCollectionRowCount("order.details", 0);
		execute("Collection.new", "viewObject=xava_view_order_details");
		setValue("product.number", "1");
		assertValue("product.description", "MULTAS DE TRAFICO");
		setValue("quantity", "66");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("order.details", 1);
		assertValueInCollection("order.details", 0, 0, "1");
		assertValueInCollection("order.details", 0, 1, "MULTAS DE TRAFICO");
		assertValueInCollection("order.details", 0, 2, "66");

		// tmp ¿Comprobar que el IssueOrder y Orde se graban?
		// tmp Falta borrar los datos
	}
			
}
