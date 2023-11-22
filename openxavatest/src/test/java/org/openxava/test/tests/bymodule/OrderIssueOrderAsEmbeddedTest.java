package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class OrderIssueOrderAsEmbeddedTest extends ModuleTestBase {
	
	public OrderIssueOrderAsEmbeddedTest(String testName) {
		super(testName, "OrderIssueOrderAsEmbedded");		
	}
	
	public void testCreateCollectionElementInAnEmbeddedReference() throws Exception {
		// It failed with OrderIssue, Order and OrderDetail, but not in other cases like Invoice with Customer and DeliverPlace,
		// perhaps because OrderIssue, Order and OrderDetail are Identifiable
		assertListRowCount(1); 
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

		execute("Mode.list");
		execute("List.orderBy", "property=description"); 
		assertListRowCount(2);
		assertValueInList(0, 1, "JUNIT ORDER ISSUE");
		execute("List.viewDetail", "row=0");
		assertValue("date", "5/14/2021");
		assertValue("description", "JUNIT ORDER ISSUE");
		assertValue("order.customer.number", "1");
		assertValue("order.customer.name", "Javi");
		assertValue("order.remarks", "JUNIT ORDER CREATED FROM ORDER ISSUE");
		assertCollectionRowCount("order.details", 1);
		assertValueInCollection("order.details", 0, 0, "1");
		assertValueInCollection("order.details", 0, 1, "MULTAS DE TRAFICO");
		assertValueInCollection("order.details", 0, 2, "66");

		String orderYear = getValue("order.year");
		String orderNumber = getValue("order.number");

		execute("CRUD.delete");
		assertNoErrors();
		changeModule("Order");
		setConditionValues(orderYear, orderNumber);
		execute("List.filter");
		assertValueInList(0, 0, orderYear);
		assertValueInList(0, 1, orderNumber);
		assertValueInList(0, 5, "JUNIT ORDER CREATED FROM ORDER ISSUE");
		execute("CRUD.deleteRow", "row=0");
		assertNoErrors();
	}
			
}
